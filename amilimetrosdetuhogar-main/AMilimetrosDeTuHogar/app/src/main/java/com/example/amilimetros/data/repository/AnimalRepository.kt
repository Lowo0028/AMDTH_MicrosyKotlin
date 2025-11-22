package com.example.amilimetros.data.repository



import com.example.amilimetros.data.local.animal.AnimalDao
import com.example.amilimetros.data.local.animal.AnimalEntity
import kotlinx.coroutines.flow.Flow

class AnimalRepository(private val animalDao: AnimalDao) {

    fun getAvailableAnimals(): Flow<List<AnimalEntity>> = animalDao.getAvailableFlow()

    fun getAllAnimals(): Flow<List<AnimalEntity>> = animalDao.getAllFlow()

    suspend fun getAnimalById(id: Long): AnimalEntity? = animalDao.getById(id)

    suspend fun addAnimal(animal: AnimalEntity): Result<Long> {
        return try {
            val id = animalDao.insert(animal)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateAnimal(animal: AnimalEntity): Result<Unit> {
        return try {
            animalDao.update(animal)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAnimal(animal: AnimalEntity): Result<Unit> {
        return try {
            animalDao.delete(animal)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun adoptAnimal(animal: AnimalEntity): Result<Unit> {
        return try {
            animalDao.update(animal.copy(isAdopted = true))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}