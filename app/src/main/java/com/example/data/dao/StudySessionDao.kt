package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.data.model.StudySession
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC LIMIT 20")
    fun getRecentSessions(): Flow<List<StudySession>>

    @Insert
    suspend fun insertSession(session: StudySession): Long

    @Query("SELECT SUM(durationSeconds) FROM study_sessions")
    fun getTotalStudyTime(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM study_sessions")
    fun getTotalSessionCount(): Flow<Int>
}
