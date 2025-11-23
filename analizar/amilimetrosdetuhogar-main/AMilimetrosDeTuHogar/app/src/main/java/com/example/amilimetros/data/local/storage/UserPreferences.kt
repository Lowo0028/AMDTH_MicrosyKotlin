// ========================================
// 📁 data/local/storage/UserPreferences.kt
// ========================================
package com.example.amilimetros.data.local.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Extensión para crear el DataStore
val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    // ========== KEYS ==========
    private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    private val USER_ID_KEY = longPreferencesKey("user_id")
    private val IS_ADMIN_KEY = booleanPreferencesKey("is_admin")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    private val USER_PHONE_KEY = stringPreferencesKey("user_phone")

    // ========== FLOWS (para observar cambios) ==========

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_LOGGED_IN_KEY] ?: false
    }

    val userId: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[USER_ID_KEY] ?: 0L
    }

    val isAdmin: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_ADMIN_KEY] ?: false
    }

    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME_KEY] ?: ""
    }

    val userEmail: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USER_EMAIL_KEY] ?: ""
    }

    val userPhone: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USER_PHONE_KEY] ?: ""
    }

    // ========== GETTERS SÍNCRONOS (para usar en ViewModels) ==========

    suspend fun getIsLoggedIn(): Boolean {
        return context.dataStore.data.map { prefs ->
            prefs[IS_LOGGED_IN_KEY] ?: false
        }.first()
    }

    suspend fun getUserId(): Long? {
        val id = context.dataStore.data.map { prefs ->
            prefs[USER_ID_KEY] ?: 0L
        }.first()
        return if (id > 0) id else null
    }

    suspend fun getIsAdmin(): Boolean {
        return context.dataStore.data.map { prefs ->
            prefs[IS_ADMIN_KEY] ?: false
        }.first()
    }

    suspend fun getNombre(): String? {
        val name = context.dataStore.data.map { prefs ->
            prefs[USER_NAME_KEY] ?: ""
        }.first()
        return name.ifBlank { null }
    }

    suspend fun getEmail(): String? {
        val email = context.dataStore.data.map { prefs ->
            prefs[USER_EMAIL_KEY] ?: ""
        }.first()
        return email.ifBlank { null }
    }

    suspend fun getTelefono(): String? {
        val phone = context.dataStore.data.map { prefs ->
            prefs[USER_PHONE_KEY] ?: ""
        }.first()
        return phone.ifBlank { null }
    }

    // ========== SETTERS (suspend functions) ==========

    suspend fun setLoggedIn(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN_KEY] = value
        }
    }

    suspend fun setUserId(id: Long) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = id
        }
    }

    suspend fun setIsAdmin(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_ADMIN_KEY] = value
        }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME_KEY] = name
        }
    }

    suspend fun setUserEmail(email: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_EMAIL_KEY] = email
        }
    }

    suspend fun setUserPhone(phone: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_PHONE_KEY] = phone
        }
    }

    // ========== FUNCIÓN PARA GUARDAR USUARIO COMPLETO ==========

    suspend fun saveUser(
        userId: Long,
        nombre: String,
        email: String,
        telefono: String,
        isAdmin: Boolean
    ) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN_KEY] = true
            prefs[USER_ID_KEY] = userId
            prefs[USER_NAME_KEY] = nombre
            prefs[USER_EMAIL_KEY] = email
            prefs[USER_PHONE_KEY] = telefono
            prefs[IS_ADMIN_KEY] = isAdmin
        }
    }

    // Alias para compatibilidad con código existente
    suspend fun saveUserSession(
        userId: Long,
        userName: String,
        userEmail: String,
        userPhone: String,
        isAdmin: Boolean
    ) {
        saveUser(userId, userName, userEmail, userPhone, isAdmin)
    }

    // ========== CLEAR USER (LOGOUT) ==========

    suspend fun clearUser() {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN_KEY] = false
            prefs[USER_ID_KEY] = 0L
            prefs[IS_ADMIN_KEY] = false
            prefs[USER_NAME_KEY] = ""
            prefs[USER_EMAIL_KEY] = ""
            prefs[USER_PHONE_KEY] = ""
        }
    }

    // Alias para compatibilidad
    suspend fun logout() {
        clearUser()
    }
}