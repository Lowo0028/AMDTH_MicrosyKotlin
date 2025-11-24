package com.example.amilimetros.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amilimetros.data.local.storage.UserPreferences
import com.example.amilimetros.data.remote.dto.UsuarioResponse
import com.example.amilimetros.data.repository.AuthApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthApiRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _userData = MutableStateFlow<UsuarioResponse?>(null)
    val userData: StateFlow<UsuarioResponse?> = _userData

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val userId = userPreferences.getUserId()
                if (userId != null) {
                    val result = authRepository.buscarPorId(userId)
                    if (result.isSuccess) {
                        _userData.value = result.getOrNull()
                    } else {
                        _errorMessage.value = "Error al cargar perfil"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.logout()
        }
    }

    fun refreshProfile() {
        loadUserData()
    }
}