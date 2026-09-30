package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.GestationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GestationDao {
    @Query("SELECT * FROM gestations ORDER BY expectedDeliveryDate ASC")
    fun getAllGestations(): Flow<List<GestationEntity>>

    @Query("SELECT * FROM gestations WHERE status = 'in_progress' ORDER BY expectedDeliveryDate ASC")
    fun getActiveGestations(): Flow<List<GestationEntity>>

    @Query("SELECT * FROM gestations WHERE eweTag = :eweTag ORDER BY serviceDate DESC")
    fun getGestationsForEwe(eweTag: String): Flow<List<GestationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGestation(gestation: GestationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(gestations: List<GestationEntity>)

    @Update
    suspend fun updateGestation(gestation: GestationEntity)
}
