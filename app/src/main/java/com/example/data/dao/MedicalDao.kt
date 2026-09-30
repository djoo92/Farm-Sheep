package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.MedicalLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicalDao {
    @Query("SELECT * FROM medical_logs ORDER BY date DESC")
    fun getAllLogs(): Flow<List<MedicalLogEntity>>

    @Query("SELECT * FROM medical_logs WHERE animalTag = :animalTag ORDER BY date DESC")
    fun getLogsForAnimal(animalTag: String): Flow<List<MedicalLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: MedicalLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<MedicalLogEntity>)
}
