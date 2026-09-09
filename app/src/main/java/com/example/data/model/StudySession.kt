package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: Long,
    val deckTitle: String,
    val mode: String,
    val scorePercentage: Int,
    val cardsReviewed: Int,
    val durationSeconds: Int,
    val timestamp: Long = System.currentTimeMillis()
)
