package com.example.amilimetros.data.repository

import com.example.amilimetros.data.local.adoption.AdoptionFormDao
import com.example.amilimetros.data.local.adoption.AdoptionFormEntity
import kotlinx.coroutines.flow.Flow

class AdoptionRepository(private val adoptionFormDao: AdoptionFormDao) {

    fun getUserForms(userId: Long): Flow<List<AdoptionFormEntity>> =
        adoptionFormDao.getByUser(userId)

    fun getAllForms(): Flow<List<AdoptionFormEntity>> =
        adoptionFormDao.getAll()

    suspend fun submitForm(form: AdoptionFormEntity): Result<Long> {
        return try {
            val id = adoptionFormDao.insert(form)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFormById(id: Long): AdoptionFormEntity? =
        adoptionFormDao.getById(id)

    suspend fun updateFormStatus(form: AdoptionFormEntity, newStatus: String): Result<Unit> {
        return try {
            adoptionFormDao.update(form.copy(status = newStatus))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}