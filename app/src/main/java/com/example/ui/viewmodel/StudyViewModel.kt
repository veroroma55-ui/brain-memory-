package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.BinauralAudioEngine
import com.example.audio.BrainwaveMode
import com.example.data.AppDatabase
import com.example.data.model.Deck
import com.example.data.model.StudyCard
import com.example.data.model.StudySession
import com.example.data.repository.StudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    DECK_DETAIL,
    CLOZE_STUDY,
    MEMORY_PALACE,
    BLURTING_BLITZ,
    FEYNMAN_STUDY,
    LEITNER_STUDY,
    BIONIC_STUDY
}

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = StudyRepository(
        database.deckDao(),
        database.studyCardDao(),
        database.studySessionDao()
    )

    val allDecks: StateFlow<List<Deck>> = repository.allDecks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val totalCardsCount: StateFlow<Int> = repository.totalCardsCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val masteredCardsCount: StateFlow<Int> = repository.masteredCardsCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val recentSessions: StateFlow<List<StudySession>> = repository.recentSessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val totalStudyTime: StateFlow<Int?> = repository.totalStudyTime.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // Navigation and Study State
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedDeck = MutableStateFlow<Deck?>(null)
    val selectedDeck: StateFlow<Deck?> = _selectedDeck.asStateFlow()

    private val _currentDeckCards = MutableStateFlow<List<StudyCard>>(emptyList())
    val currentDeckCards: StateFlow<List<StudyCard>> = _currentDeckCards.asStateFlow()
    val activeDeckCards: StateFlow<List<StudyCard>> = _currentDeckCards.asStateFlow()

    private val _activeCardIndex = MutableStateFlow(0)
    val activeCardIndex: StateFlow<Int> = _activeCardIndex.asStateFlow()

    // Brainwave Audio Engine
    val audioEngine = BinauralAudioEngine()
    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()

    private val _audioBrainwaveMode = MutableStateFlow(BrainwaveMode.ALPHA)
    val audioBrainwaveMode: StateFlow<BrainwaveMode> = _audioBrainwaveMode.asStateFlow()
    val activeBrainwaveMode: StateFlow<BrainwaveMode> = _audioBrainwaveMode.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureDefaultDataSeeded()
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectDeck(deck: Deck) {
        _selectedDeck.value = deck
        _activeCardIndex.value = 0
        viewModelScope.launch {
            repository.getCardsForDeck(deck.id).collect { cards ->
                _currentDeckCards.value = cards
            }
        }
    }

    fun startStudyMode(deck: Deck, screen: AppScreen) {
        selectDeck(deck)
        _currentScreen.value = screen
    }

    fun nextCard() {
        if (_activeCardIndex.value < _currentDeckCards.value.size - 1) {
            _activeCardIndex.value += 1
        }
    }

    fun prevCard() {
        if (_activeCardIndex.value > 0) {
            _activeCardIndex.value -= 1
        }
    }

    fun setCardIndex(index: Int) {
        if (index in _currentDeckCards.value.indices) {
            _activeCardIndex.value = index
        }
    }

    fun reviewCard(card: StudyCard, isCorrect: Boolean) {
        viewModelScope.launch {
            repository.processCardReview(card, isCorrect)
        }
    }

    fun recordStudySession(modeName: String, scorePct: Int, durationSec: Int) {
        val deck = _selectedDeck.value ?: return
        viewModelScope.launch {
            repository.recordSession(
                StudySession(
                    deckId = deck.id,
                    deckTitle = deck.title,
                    mode = modeName,
                    scorePercentage = scorePct,
                    cardsReviewed = _currentDeckCards.value.size,
                    durationSeconds = durationSec
                )
            )
        }
    }

    fun createDeck(title: String, category: String, description: String, icon: String, colorHex: String) {
        viewModelScope.launch {
            val newDeck = Deck(
                title = title,
                category = category,
                description = description,
                iconName = icon,
                colorHex = colorHex
            )
            val newId = repository.insertDeck(newDeck)
            // Select immediately
            selectDeck(newDeck.copy(id = newId))
        }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch {
            repository.deleteDeck(deck)
            if (_selectedDeck.value?.id == deck.id) {
                _selectedDeck.value = null
                _currentScreen.value = AppScreen.HOME
            }
        }
    }

    fun deleteCurrentDeck() {
        val deck = _selectedDeck.value ?: return
        deleteDeck(deck)
    }

    fun recordBlurtingSession(scorePct: Int, durationSec: Int) {
        recordStudySession("Blurting Blitz", scorePct, durationSec)
    }

    fun addCardToDeck(
        title: String,
        fullExplanation: String,
        keyTerms: String,
        mnemonicStory: String,
        memoryPalaceRoom: String,
        sensoryAnchorEmoji: String,
        colorHex: String,
        feynmanPrompt: String
    ) {
        val deckId = _selectedDeck.value?.id ?: return
        addCard(
            deckId = deckId,
            title = title,
            explanation = fullExplanation,
            keywords = keyTerms,
            mnemonic = mnemonicStory,
            palaceRoom = memoryPalaceRoom,
            emoji = sensoryAnchorEmoji,
            colorHex = colorHex,
            feynmanPrompt = feynmanPrompt
        )
    }

    fun addCard(
        deckId: Long,
        title: String,
        explanation: String,
        keywords: String,
        mnemonic: String,
        palaceRoom: String,
        emoji: String,
        colorHex: String,
        feynmanPrompt: String
    ) {
        viewModelScope.launch {
            val card = StudyCard(
                deckId = deckId,
                title = title,
                fullExplanation = explanation,
                keyTerms = keywords,
                mnemonicStory = mnemonic,
                memoryPalaceRoom = palaceRoom,
                sensoryAnchorEmoji = emoji,
                sensoryColorHex = colorHex,
                feynmanPrompt = feynmanPrompt
            )
            repository.insertCard(card)
        }
    }

    fun toggleAudio() {
        if (_isAudioPlaying.value) {
            audioEngine.stop()
            _isAudioPlaying.value = false
        } else {
            audioEngine.start(viewModelScope, _audioBrainwaveMode.value)
            _isAudioPlaying.value = true
        }
    }

    fun setBrainwaveMode(mode: BrainwaveMode) {
        _audioBrainwaveMode.value = mode
        if (_isAudioPlaying.value) {
            audioEngine.start(viewModelScope, mode)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.stop()
    }
}
