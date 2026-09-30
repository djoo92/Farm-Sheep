package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AnimalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {
    @Query("SELECT * FROM animals ORDER BY tagId ASC")
    fun getAllAnimals(): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE tagId = :tagId LIMIT 1")
    fun getAnimalByTag(tagId: String): Flow<AnimalEntity?>

    @Query("SELECT * FROM animals WHERE tagId = :tagId LIMIT 1")
    suspend fun getAnimalByTagOnce(tagId: String): AnimalEntity?

    @Query("SELECT * FROM animals WHERE reproductiveStatus = 'pregnant' ORDER BY expectedLambingDate ASC")
    fun getPregnantAnimals(): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE groupId = :groupId ORDER BY tagId ASC")
    fun getAnimalsByGroup(groupId: String): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE damTag = :damTag OR sireTag = :damTag")
    fun getOffspring(damTag: String): Flow<List<AnimalEntity>>

    @Query("SELECT COUNT(*) FROM animals")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM animals WHERE reproductiveStatus = 'pregnant'")
    fun getPregnantCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM animals WHERE category IN ('nursing_lamb_male', 'nursing_lamb_female', 'fattening_lamb')")
    fun getLambsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM animals WHERE gender = 'male' AND category IN ('breeding_ram', 'juvenile_ram')")
    fun getRamsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM animals WHERE gender = 'female' AND category IN ('primiparous_ewe', 'multiparous_ewe')")
    fun getEwesCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimal(animal: AnimalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(animals: List<AnimalEntity>)

    @Update
    suspend fun updateAnimal(animal: AnimalEntity)

    @Delete
    suspend fun deleteAnimal(animal: AnimalEntity)

    @Query("DELETE FROM animals WHERE tagId = :tagId")
    suspend fun deleteByTag(tagId: String)
}
