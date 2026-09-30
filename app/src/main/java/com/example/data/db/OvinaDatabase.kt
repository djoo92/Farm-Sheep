package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AnimalDao
import com.example.data.dao.ChatDao
import com.example.data.dao.GestationDao
import com.example.data.dao.HerdGroupDao
import com.example.data.dao.MedicalDao
import com.example.data.model.AnimalEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.GestationEntity
import com.example.data.model.HerdGroupEntity
import com.example.data.model.MedicalLogEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AnimalEntity::class,
        HerdGroupEntity::class,
        GestationEntity::class,
        MedicalLogEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class OvinaDatabase : RoomDatabase() {
    abstract fun animalDao(): AnimalDao
    abstract fun herdGroupDao(): HerdGroupDao
    abstract fun gestationDao(): GestationDao
    abstract fun medicalDao(): MedicalDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: OvinaDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): OvinaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OvinaDatabase::class.java,
                    "ovina_farm_database.db"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database)
                }
            }
        }

        private suspend fun populateDatabase(db: OvinaDatabase) {
            db.herdGroupDao().insertAll(SeedData.getInitialGroups())
            db.animalDao().insertAll(SeedData.getInitialAnimals())
            db.gestationDao().insertAll(SeedData.getInitialGestations())
            db.medicalDao().insertAll(SeedData.getInitialMedicalLogs())
            db.chatDao().insertAll(SeedData.getInitialChatMessages())
        }
    }
}
