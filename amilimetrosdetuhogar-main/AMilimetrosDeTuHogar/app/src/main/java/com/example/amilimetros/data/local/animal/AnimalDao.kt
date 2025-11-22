package com.example.amilimetros.data.local.animal


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(animal: AnimalEntity): Long

    @Update
    suspend fun update(animal: AnimalEntity)

    @Delete
    suspend fun delete(animal: AnimalEntity)

    @Query("SELECT * FROM animals WHERE isAdopted = 0 ORDER BY name ASC")
    fun getAvailableFlow(): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals ORDER BY name ASC")
    fun getAllFlow(): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE id = :id")
    suspend fun getById(id: Long): AnimalEntity?

    @Query("SELECT COUNT(*) FROM animals")
    suspend fun count(): Int
}