package com.example.amilimetros.data.repository

import com.example.amilimetros.data.remote.ApiService
import com.example.amilimetros.data.remote.dto.LoginRequest
import com.example.amilimetros.data.remote.dto.RegisterRequest
import com.example.amilimetros.data.remote.dto.UsuarioResponse

class AuthApiRepository {

    private val api = ApiService.createAuthService()

    suspend fun login(request: LoginRequest): Result<UsuarioResponse> {
        return try {
            val response = api.login(request)

            if (response.isSuccessful) {
                // Buscar usuario por correo para obtener datos completos
                val userResponse = api.buscarUsuarioPorCorreo(request.email)
                if (userResponse.isSuccessful && userResponse.body() != null) {
                    Result.success(userResponse.body()!!)
                } else {
                    Result.failure(Exception("Error al obtener datos del usuario"))
                }
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<UsuarioResponse> {
        return try {
            val response = api.register(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al registrar"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun buscarPorCorreo(correo: String): Result<UsuarioResponse> {
        return try {
            val response = api.buscarUsuarioPorCorreo(correo)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun buscarPorId(id: Long): Result<UsuarioResponse> {
        return try {
            val response = api.buscarUsuarioPorId(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}