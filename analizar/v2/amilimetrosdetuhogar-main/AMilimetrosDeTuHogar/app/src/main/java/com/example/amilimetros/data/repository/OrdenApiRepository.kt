package com.example.amilimetros.data.repository

import com.example.amilimetros.data.remote.ApiService
import com.example.amilimetros.data.remote.dto.*

class OrdenApiRepository {

    private val api = ApiService.createOrdenService()

    suspend fun obtenerOrdenesPorUsuario(usuarioId: Long): Result<List<OrdenDto>> {
        return try {
            val response = api.obtenerOrdenesPorUsuario(usuarioId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener órdenes"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerOrdenPorId(ordenId: Long): Result<OrdenDto> {
        return try {
            val response = api.obtenerOrdenPorId(ordenId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Orden no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerItemsDeOrden(ordenId: Long): Result<List<ItemOrdenDto>> {
        return try {
            val response = api.obtenerItemsDeOrden(ordenId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener items"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerDetallesOrden(ordenId: Long): Result<DetallesOrdenDto> {
        return try {
            val response = api.obtenerDetallesOrden(ordenId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener detalles"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearOrden(request: CrearOrdenRequest): Result<OrdenDto> {
        return try {
            val response = api.crearOrden(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al crear orden"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelarOrden(ordenId: Long): Result<OrdenDto> {
        return try {
            val response = api.cancelarOrden(ordenId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cancelar orden"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerTodasLasOrdenes(): Result<List<OrdenDto>> {
        return try {
            val response = api.obtenerTodasLasOrdenes()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener órdenes"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}