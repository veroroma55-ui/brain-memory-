package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.StudyCard
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyCardDao {
    @Query("SELECT * FROM cards WHERE deckId = :deckId ORDER BY id ASC")
    fun getCardsForDeck(deckId: Long): Flow<List<StudyCard>>

    @Query("SELECT * FROM cards WHERE deckId = :deckId ORDER BY id ASC")
    suspend fun getCardsForDeckSync(deckId: Long): List<StudyCard>

    @Query("SELECT * FROM cards WHERE nextReviewTimestamp <= :currentTimestamp ORDER BY nextReviewTimestamp ASC")
    fun getDueCards(currentTimestamp: Long = System.currentTimeMillis()): Flow<List<StudyCard>>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getCardById(id: Long): StudyCard?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: StudyCard): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<StudyCard>)

    @Update
    suspend fun updateCard(card: StudyCard)

    @Delete
    suspend fun deleteCard(card: StudyCard)

    @Query("SELECT COUNT(*) FROM cards")
    fun getTotalCardCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM cards WHERE isMastered = 1")
    fun getMasteredCardCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM cards WHERE deckId = :deckId")
    fun getCardCountForDeck(deckId: Long): Flow<Int>
}
