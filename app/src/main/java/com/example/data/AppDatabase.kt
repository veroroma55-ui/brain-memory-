package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.DeckDao
import com.example.data.dao.StudyCardDao
import com.example.data.dao.StudySessionDao
import com.example.data.model.Deck
import com.example.data.model.StudyCard
import com.example.data.model.StudySession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Deck::class, StudyCard::class, StudySession::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun studyCardDao(): StudyCardDao
    abstract fun studySessionDao(): StudySessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "brain_memory_study_db"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val scope: CoroutineScope) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.deckDao(), database.studyCardDao())
                    }
                }
            }

            suspend fun populateInitialData(deckDao: DeckDao, cardDao: StudyCardDao) {
                DefaultDecksData.initialDecks.forEach { deck ->
                    deckDao.insertDeck(deck)
                }
                cardDao.insertCards(DefaultDecksData.initialCards)
            }
        }
    }
}
