package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.BionicSpeedScreen
import com.example.ui.screens.BlurtingScreen
import com.example.ui.screens.ClozeStudyScreen
import com.example.ui.screens.DeckDetailScreen
import com.example.ui.screens.FeynmanScreen
import com.example.ui.screens.FocusAudioDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeitnerScreen
import com.example.ui.screens.MemoryPalaceScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.StudyViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        BrainMemoryApp()
      }
    }
  }
}

@Composable
fun BrainMemoryApp(
  viewModel: StudyViewModel = viewModel()
) {
  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  val allDecks by viewModel.allDecks.collectAsStateWithLifecycle()
  val selectedDeck by viewModel.selectedDeck.collectAsStateWithLifecycle()
  val activeCards by viewModel.activeDeckCards.collectAsStateWithLifecycle()
  val activeCardIndex by viewModel.activeCardIndex.collectAsStateWithLifecycle()
  val isAudioPlaying by viewModel.isAudioPlaying.collectAsStateWithLifecycle()
  val activeBrainwaveMode by viewModel.activeBrainwaveMode.collectAsStateWithLifecycle()
  val totalCardsCount by viewModel.totalCardsCount.collectAsStateWithLifecycle()
  val masteredCardsCount by viewModel.masteredCardsCount.collectAsStateWithLifecycle()

  var showAudioDialog by remember { mutableStateOf(false) }

  // System back handler
  BackHandler(enabled = currentScreen != AppScreen.HOME) {
    if (currentScreen == AppScreen.DECK_DETAIL) {
      viewModel.navigateTo(AppScreen.HOME)
    } else if (selectedDeck != null) {
      viewModel.navigateTo(AppScreen.DECK_DETAIL)
    } else {
      viewModel.navigateTo(AppScreen.HOME)
    }
  }

  when (currentScreen) {
    AppScreen.HOME -> {
      HomeScreen(
        decks = allDecks,
        totalCards = totalCardsCount,
        masteredCards = masteredCardsCount,
        onSelectDeck = { deck ->
          viewModel.selectDeck(deck)
        },
        onLaunchGlobalMethod = { targetScreen ->
          if (allDecks.isNotEmpty()) {
            if (selectedDeck == null) {
              viewModel.selectDeck(allDecks.first())
            }
            viewModel.navigateTo(targetScreen)
          }
        },
        onCreateDeck = { title, category, description, icon, colorHex ->
          viewModel.createDeck(title, category, description, icon, colorHex)
        },
        isAudioPlaying = isAudioPlaying,
        onToggleAudio = { viewModel.toggleAudio() },
        onOpenAudioSettings = { showAudioDialog = true }
      )
    }

    AppScreen.DECK_DETAIL -> {
      val deck = selectedDeck ?: allDecks.firstOrNull()
      if (deck != null) {
        DeckDetailScreen(
          deck = deck,
          cards = activeCards,
          onLaunchStudyMode = { targetScreen ->
            viewModel.navigateTo(targetScreen)
          },
          onAddCard = { title, explanation, keywords, mnemonic, palaceRoom, emoji, colorHex, feynmanPrompt ->
            viewModel.addCardToDeck(
              title = title,
              fullExplanation = explanation,
              keyTerms = keywords,
              mnemonicStory = mnemonic,
              memoryPalaceRoom = palaceRoom,
              sensoryAnchorEmoji = emoji,
              colorHex = colorHex,
              feynmanPrompt = feynmanPrompt
            )
          },
          onDeleteDeck = {
            viewModel.deleteCurrentDeck()
          },
          onBack = {
            viewModel.navigateTo(AppScreen.HOME)
          },
          isAudioPlaying = isAudioPlaying,
          onToggleAudio = { viewModel.toggleAudio() },
          onOpenAudioSettings = { showAudioDialog = true }
        )
      } else {
        viewModel.navigateTo(AppScreen.HOME)
      }
    }

    AppScreen.CLOZE_STUDY -> {
      ClozeStudyScreen(
        cards = activeCards,
        activeCardIndex = activeCardIndex,
        onNextCard = { viewModel.nextCard() },
        onPrevCard = { viewModel.prevCard() },
        onCardReviewed = { card, remembered ->
          viewModel.reviewCard(card, remembered)
        },
        onBack = {
          viewModel.navigateTo(if (selectedDeck != null) AppScreen.DECK_DETAIL else AppScreen.HOME)
        },
        isAudioPlaying = isAudioPlaying,
        onToggleAudio = { viewModel.toggleAudio() },
        onOpenAudioSettings = { showAudioDialog = true }
      )
    }

    AppScreen.MEMORY_PALACE -> {
      MemoryPalaceScreen(
        cards = activeCards,
        onBack = {
          viewModel.navigateTo(if (selectedDeck != null) AppScreen.DECK_DETAIL else AppScreen.HOME)
        },
        isAudioPlaying = isAudioPlaying,
        onToggleAudio = { viewModel.toggleAudio() },
        onOpenAudioSettings = { showAudioDialog = true }
      )
    }

    AppScreen.BLURTING_BLITZ -> {
      BlurtingScreen(
        cards = activeCards,
        activeCardIndex = activeCardIndex,
        onNextCard = { viewModel.nextCard() },
        onSaveSession = { score, duration ->
          viewModel.recordBlurtingSession(score, duration)
        },
        onBack = {
          viewModel.navigateTo(if (selectedDeck != null) AppScreen.DECK_DETAIL else AppScreen.HOME)
        },
        isAudioPlaying = isAudioPlaying,
        onToggleAudio = { viewModel.toggleAudio() },
        onOpenAudioSettings = { showAudioDialog = true }
      )
    }

    AppScreen.FEYNMAN_STUDY -> {
      FeynmanScreen(
        cards = activeCards,
        activeCardIndex = activeCardIndex,
        onNextCard = { viewModel.nextCard() },
        onPrevCard = { viewModel.prevCard() },
        onBack = {
          viewModel.navigateTo(if (selectedDeck != null) AppScreen.DECK_DETAIL else AppScreen.HOME)
        },
        isAudioPlaying = isAudioPlaying,
        onToggleAudio = { viewModel.toggleAudio() },
        onOpenAudioSettings = { showAudioDialog = true }
      )
    }

    AppScreen.LEITNER_STUDY -> {
      LeitnerScreen(
        cards = activeCards,
        activeCardIndex = activeCardIndex,
        onNextCard = { viewModel.nextCard() },
        onPrevCard = { viewModel.prevCard() },
        onCardReviewed = { card, remembered ->
          viewModel.reviewCard(card, remembered)
        },
        onBack = {
          viewModel.navigateTo(if (selectedDeck != null) AppScreen.DECK_DETAIL else AppScreen.HOME)
        },
        isAudioPlaying = isAudioPlaying,
        onToggleAudio = { viewModel.toggleAudio() },
        onOpenAudioSettings = { showAudioDialog = true }
      )
    }

    AppScreen.BIONIC_STUDY -> {
      BionicSpeedScreen(
        cards = activeCards,
        activeCardIndex = activeCardIndex,
        onNextCard = { viewModel.nextCard() },
        onPrevCard = { viewModel.prevCard() },
        onBack = {
          viewModel.navigateTo(if (selectedDeck != null) AppScreen.DECK_DETAIL else AppScreen.HOME)
        },
        isAudioPlaying = isAudioPlaying,
        onToggleAudio = { viewModel.toggleAudio() },
        onOpenAudioSettings = { showAudioDialog = true }
      )
    }
  }

  // Audio configuration and breathing modal
  if (showAudioDialog) {
    FocusAudioDialog(
      isPlaying = isAudioPlaying,
      currentMode = activeBrainwaveMode,
      onModeSelected = { mode ->
        viewModel.setBrainwaveMode(mode)
      },
      onTogglePlay = {
        viewModel.toggleAudio()
      },
      onDismiss = {
        showAudioDialog = false
      }
    )
  }
}

