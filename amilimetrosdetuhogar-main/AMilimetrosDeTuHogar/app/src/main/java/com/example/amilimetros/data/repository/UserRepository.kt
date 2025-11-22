package com.example.amilimetros.data.repository

import com.example.amilimetros.data.local.user.UserDao
import com.example.amilimetros.data.local.user.UserEntity

// Repositorio: orquesta reglas de negocio para login/registro sobre el DAO
class UserRepository(
    private val userDao: UserDao // Inyección del DAO
) {

    // ========== LOGIN ==========
    // Busca por email y valida contraseña
    suspend fun login(email: String, password: String): Result<UserEntity> {
        return try {
            val user = userDao.getByEmail(email)
            if (user != null && user.password == password) {
                Result.success(user) // ✅ Éxito - devuelve el usuario completo
            } else {
                Result.failure(IllegalArgumentException("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== REGISTRO ==========
    // Valida que el email no esté duplicado y crea nuevo usuario
    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<Long> {
        return try {
            val exists = userDao.getByEmail(email) != null
            if (exists) {
                return Result.failure(IllegalStateException("El correo ya está registrado"))
            }

            val id = userDao.insert(
                UserEntity(
                    name = name,
                    email = email,
                    phone = phone,
                    password = password,
                    isAdmin = false // Los usuarios nuevos NO son admin por defecto
                )
            )
            Result.success(id) // Devuelve el ID generado
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== OBTENER USUARIO POR ID ==========
    suspend fun getUserById(id: Long): Result<UserEntity?> {
        return try {
            val user = userDao.getById(id)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== OBTENER USUARIO POR EMAIL ==========
    suspend fun getUserByEmail(email: String): Result<UserEntity?> {
        return try {
            val user = userDao.getByEmail(email)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== VERIFICAR SI ES ADMINISTRADOR ==========
    suspend fun isAdmin(userId: Long): Boolean {
        return try {
            val user = userDao.getById(userId)
            user?.isAdmin ?: false
        } catch (e: Exception) {
            false
        }
    }

    // ========== OBTENER TODOS LOS USUARIOS (solo para admin) ==========
    suspend fun getAllUsers(): Result<List<UserEntity>> {
        return try {
            val users = userDao.getAll()
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== CONTAR USUARIOS (para verificar si hay datos) ==========
    suspend fun count(): Int {
        return try {
            userDao.count()
        } catch (e: Exception) {
            0
        }
    }

    // ========== ACTUALIZAR USUARIO (opcional - para perfil) ==========
    suspend fun updateUser(user: UserEntity): Result<Unit> {
        return try {
            userDao.update(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== ELIMINAR USUARIO (solo admin) ==========
    suspend fun deleteUser(user: UserEntity): Result<Unit> {
        return try {
            userDao.delete(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== CAMBIAR CONTRASEÑA ==========
    suspend fun changePassword(
        userId: Long,
        oldPassword: String,
        newPassword: String
    ): Result<Unit> {
        return try {
            val user = userDao.getById(userId)
            if (user == null) {
                return Result.failure(IllegalArgumentException("Usuario no encontrado"))
            }

            if (user.password != oldPassword) {
                return Result.failure(IllegalArgumentException("Contraseña actual incorrecta"))
            }

            userDao.update(user.copy(password = newPassword))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}