package com.example.amilimetros.data.repository

import com.example.amilimetros.data.remote.ApiService
import com.example.amilimetros.data.remote.dto.FormularioAdopcionDto

class FormularioApiRepository {

    private val api = ApiService.createFormularioService()

    suspend fun obtenerTodos(emailAdmin: String): Result<List<FormularioAdopcionDto>> {
        return try {
            val response = api.obtenerTodosFormularios(emailAdmin)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al cargar formularios"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerPorId(id: Long): Result<FormularioAdopcionDto> {
        return try {
            val response = api.obtenerFormularioPorId(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Formulario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerPorUsuario(usuarioId: Long): Result<List<FormularioAdopcionDto>> {
        return try {
            val response = api.obtenerFormulariosPorUsuario(usuarioId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al cargar formularios"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerPorAnimal(animalId: Long): Result<List<FormularioAdopcionDto>> {
        return try {
            val response = api.obtenerFormulariosPorAnimal(animalId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al cargar formularios"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearFormulario(
        usuarioId: Long,
        animalId: Long,
        direccion: String,
        tipoVivienda: String,
        tieneMallasVentanas: Boolean,
        viveEnDepartamento: Boolean,
        tieneOtrosAnimales: Boolean,
        motivoAdopcion: String
    ): Result<FormularioAdopcionDto> {
        return try {
            val body = mapOf(
                "direccion" to direccion,
                "tipoVivienda" to tipoVivienda,
                "tieneMallasVentanas" to tieneMallasVentanas,
                "viveEnDepartamento" to viveEnDepartamento,
                "tieneOtrosAnimales" to tieneOtrosAnimales,
                "motivoAdopcion" to motivoAdopcion
            )

            val response = api.crearFormulario(usuarioId, animalId, body)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al crear formulario"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun aprobarFormulario(
        id: Long,
        emailAdmin: String,
        comentarios: String?
    ): Result<FormularioAdopcionDto> {
        return try {
            val body = mapOf(
                "emailAdmin" to emailAdmin,
                "comentarios" to (comentarios ?: "")
            )

            val response = api.aprobarFormulario(id, body)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al aprobar"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rechazarFormulario(
        id: Long,
        emailAdmin: String,
        comentarios: String?
    ): Result<FormularioAdopcionDto> {
        return try {
            val body = mapOf(
                "emailAdmin" to emailAdmin,
                "comentarios" to (comentarios ?: "")
            )

            val response = api.rechazarFormulario(id, body)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al rechazar"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarFormulario(id: Long, emailAdmin: String): Result<Unit> {
        return try {
            val response = api.eliminarFormulario(id, emailAdmin)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al eliminar"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}