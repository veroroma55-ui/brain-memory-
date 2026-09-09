package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = Deck::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["deckId"])]
)
data class StudyCard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: Long,
    val title: String,
    val fullExplanation: String,
    val keyTerms: String = "",
    val mnemonicStory: String = "",
    val memoryPalaceRoom: String = "ردهة الاستقبال",
    val sensoryAnchorEmoji: String = "⚡",
    val sensoryColorHex: String = "#8B5CF6",
    val feynmanPrompt: String = "",
    val leitnerBox: Int = 1,
    val easeFactor: Float = 2.5f,
    val repetitions: Int = 0,
    val intervalDays: Int = 1,
    val nextReviewTimestamp: Long = System.currentTimeMillis(),
    val timesReviewed: Int = 0,
    val timesCorrect: Int = 0,
    val isMastered: Boolean = false
)
