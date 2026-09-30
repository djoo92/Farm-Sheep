package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.HerdGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HerdGroupDao {
    @Query("SELECT * FROM herd_groups ORDER BY name ASC")
    fun getAllGroups(): Flow<List<HerdGroupEntity>>

    @Query("SELECT * FROM herd_groups WHERE id = :id LIMIT 1")
    suspend fun getGroupById(id: String): HerdGroupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: HerdGroupEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(groups: List<HerdGroupEntity>)
}
