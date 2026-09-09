package com.example.data.repository

import com.example.data.dao.DeckDao
import com.example.data.dao.StudyCardDao
import com.example.data.dao.StudySessionDao
import com.example.data.model.Deck
import com.example.data.model.StudyCard
import com.example.data.model.StudySession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.math.max

class StudyRepository(
    private val deckDao: DeckDao,
    private val studyCardDao: StudyCardDao,
    private val studySessionDao: StudySessionDao
) {
    val allDecks: Flow<List<Deck>> = deckDao.getAllDecks()
    val totalCardsCount: Flow<Int> = studyCardDao.getTotalCardCount()
    val masteredCardsCount: Flow<Int> = studyCardDao.getMasteredCardCount()
    val recentSessions: Flow<List<StudySession>> = studySessionDao.getRecentSessions()
    val totalStudyTime: Flow<Int?> = studySessionDao.getTotalStudyTime()

    fun getCardsForDeck(deckId: Long): Flow<List<StudyCard>> =
        studyCardDao.getCardsForDeck(deckId)

    suspend fun getCardsForDeckSync(deckId: Long): List<StudyCard> =
        studyCardDao.getCardsForDeckSync(deckId)

    suspend fun insertDeck(deck: Deck): Long = deckDao.insertDeck(deck)

    suspend fun deleteDeck(deck: Deck) = deckDao.deleteDeck(deck)

    suspend fun insertCard(card: StudyCard): Long = studyCardDao.insertCard(card)

    suspend fun updateCard(card: StudyCard) = studyCardDao.updateCard(card)

    suspend fun deleteCard(card: StudyCard) = studyCardDao.deleteCard(card)

    suspend fun recordSession(session: StudySession): Long =
        studySessionDao.insertSession(session)

    /**
     * Promotes or demotes card in the Leitner spaced repetition system.
     * Box 1: 1 day, Box 2: 3 days, Box 3: 7 days, Box 4: 14 days, Box 5: 30 days (Mastered)
     */
    suspend fun processCardReview(card: StudyCard, isCorrect: Boolean) {
        val newRepetitions = if (isCorrect) card.repetitions + 1 else 0
        val newBox = if (isCorrect) {
            (card.leitnerBox + 1).coerceAtMost(5)
        } else {
            1 // demote to Box 1 for reinforcement
        }

        val daysToAdd = when (newBox) {
            1 -> 1
            2 -> 3
            3 -> 7
            4 -> 14
            5 -> 30
            else -> 1
        }

        val nextTimestamp = System.currentTimeMillis() + (daysToAdd * 24L * 60L * 60L * 1000L)
        val newEaseFactor = if (isCorrect) {
            card.easeFactor + 0.1f
        } else {
            max(1.3f, card.easeFactor - 0.2f)
        }

        val updatedCard = card.copy(
            leitnerBox = newBox,
            repetitions = newRepetitions,
            intervalDays = daysToAdd,
            nextReviewTimestamp = nextTimestamp,
            timesReviewed = card.timesReviewed + 1,
            timesCorrect = card.timesCorrect + if (isCorrect) 1 else 0,
            isMastered = newBox == 5,
            easeFactor = newEaseFactor
        )

        studyCardDao.updateCard(updatedCard)
    }

    suspend fun ensureDefaultDataSeeded() {
        val currentDecks = allDecks.firstOrNull()
        if (currentDecks.isNullOrEmpty()) {
            com.example.data.DefaultDecksData.initialDecks.forEach { deckDao.insertDeck(it) }
            studyCardDao.insertCards(com.example.data.DefaultDecksData.initialCards)
        }
    }
}
