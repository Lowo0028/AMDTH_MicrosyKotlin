package com.example.amilimetros.data.repository

import com.example.amilimetros.data.remote.ApiService
import com.example.amilimetros.data.remote.dto.AddToCartRequest
import com.example.amilimetros.data.remote.dto.CarritoItemDto

class CarritoApiRepository {

    private val api = ApiService.createCarritoService()

    suspend fun obtenerCarrito(usuarioId: Long): Result<List<CarritoItemDto>> {
        return try {
            val response = api.obtenerCarrito(usuarioId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al cargar carrito"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun agregarAlCarrito(request: AddToCartRequest): Result<CarritoItemDto> {
        return try {
            val response = api.agregarAlCarrito(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al agregar al carrito"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarCantidad(itemId: Long, cantidad: Int): Result<CarritoItemDto> {
        return try {
            val body = mapOf("cantidad" to cantidad)
            val response = api.actualizarCantidadItem(itemId, body)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al actualizar cantidad"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarItem(itemId: Long): Result<Unit> {
        return try {
            val response = api.eliminarItemCarrito(itemId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun vaciarCarrito(usuarioId: Long): Result<Unit> {
        return try {
            val response = api.vaciarCarrito(usuarioId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al vaciar carrito"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun calcularTotal(usuarioId: Long): Result<Double> {
        return try {
            val response = api.calcularTotalCarrito(usuarioId)
            if (response.isSuccessful && response.body() != null) {
                val total = response.body()?.get("total") as? Double ?: 0.0
                Result.success(total)
            } else {
                Result.failure(Exception("Error al calcular total"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}