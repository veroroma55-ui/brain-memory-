package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks")
data class Deck(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val description: String,
    val iconName: String = "brain",
    val colorHex: String = "#06B6D4",
    val createdTimestamp: Long = System.currentTimeMillis()
)
