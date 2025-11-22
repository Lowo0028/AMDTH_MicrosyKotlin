package com.example.amilimetros.data.local.adoption

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AdoptionFormDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(form: AdoptionFormEntity): Long

    @Query("SELECT * FROM adoption_forms WHERE userId = :userId ORDER BY submittedAt DESC")
    fun getByUser(userId: Long): Flow<List<AdoptionFormEntity>>

    @Query("SELECT * FROM adoption_forms ORDER BY submittedAt DESC")
    fun getAll(): Flow<List<AdoptionFormEntity>>

    @Query("SELECT * FROM adoption_forms WHERE id = :id")
    suspend fun getById(id: Long): AdoptionFormEntity?

    @Update
    suspend fun update(form: AdoptionFormEntity)

    @Delete
    suspend fun delete(form: AdoptionFormEntity)
}