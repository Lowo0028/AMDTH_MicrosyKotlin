package com.example.amilimetros.data.repository

import com.example.amilimetros.data.remote.ApiService
import com.example.amilimetros.data.remote.dto.AnimalDto

class AnimalApiRepository {

    private val api = ApiService.createAnimalesService()

    suspend fun obtenerTodos(): Result<List<AnimalDto>> {
        return try {
            val response = api.obtenerAnimales()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al cargar animales"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerDisponibles(): Result<List<AnimalDto>> {
        return try {
            val response = api.obtenerAnimalesDisponibles()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al cargar animales disponibles"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerAdoptados(): Result<List<AnimalDto>> {
        return try {
            val response = api.obtenerAnimalesAdoptados()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al cargar animales adoptados"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerPorId(id: Long): Result<AnimalDto> {
        return try {
            val response = api.obtenerAnimalPorId(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Animal no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerPorEspecie(especie: String): Result<List<AnimalDto>> {
        return try {
            val response = api.obtenerAnimalesPorEspecie(especie)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al filtrar animales"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun buscarPorNombre(nombre: String): Result<List<AnimalDto>> {
        return try {
            val response = api.buscarAnimales(nombre)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al buscar animales"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crear(animal: Map<String, Any>): Result<AnimalDto> {
        return try {
            val response = api.crearAnimal(animal)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al crear animal"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizar(id: Long, animal: Map<String, Any>): Result<AnimalDto> {
        return try {
            val response = api.actualizarAnimal(id, animal)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al actualizar animal"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun marcarComoAdoptado(id: Long, emailAdmin: String): Result<Unit> {
        return try {
            val response = api.marcarAnimalComoAdoptado(id, emailAdmin)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al marcar como adoptado"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminar(id: Long, emailAdmin: String): Result<Unit> {
        return try {
            val response = api.eliminarAnimal(id, emailAdmin)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al eliminar animal"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}