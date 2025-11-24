package com.example.amilimetros.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amilimetros.data.remote.dto.LoginRequest
import com.example.amilimetros.data.remote.dto.RegisterRequest
import com.example.amilimetros.data.remote.dto.UsuarioResponse
import com.example.amilimetros.data.repository.AuthApiRepository
import com.example.amilimetros.data.local.storage.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthApiRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    private val _currentUser = MutableStateFlow<UsuarioResponse?>(null)
    val currentUser: StateFlow<UsuarioResponse?> = _currentUser

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val userId = userPreferences.getUserId()
            val email = userPreferences.getEmail()
            val nombre = userPreferences.getNombre()
            val isAdmin = userPreferences.getIsAdmin()

            if (userId != null && email != null && nombre != null) {
                _currentUser.value = UsuarioResponse(
                    id = userId,
                    nombre = nombre,
                    email = email,
                    telefono = userPreferences.getTelefono() ?: "",
                    isAdmin = isAdmin
                )
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val request = LoginRequest(email, password)
                val result = repository.login(request)

                if (result.isSuccess) {
                    val usuario = result.getOrNull()
                    if (usuario != null) {
                        userPreferences.saveUser(
                            userId = usuario.id,
                            email = usuario.email,
                            nombre = usuario.nombre,
                            telefono = usuario.telefono,
                            isAdmin = usuario.isAdmin
                        )
                        _currentUser.value = usuario
                        _loginState.value = LoginState.Success(usuario)
                    } else {
                        _loginState.value = LoginState.Error("Error al obtener datos del usuario")
                    }
                } else {
                    _loginState.value = LoginState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun register(nombre: String, email: String, telefono: String, password: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val request = RegisterRequest(nombre, email, telefono, password)
                val result = repository.register(request)

                if (result.isSuccess) {
                    _registerState.value = RegisterState.Success
                } else {
                    _registerState.value = RegisterState.Error(
                        result.exceptionOrNull()?.message ?: "Error en el registro"
                    )
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.clearUser()
            _currentUser.value = null
            _loginState.value = LoginState.Idle
        }
    }

    fun resetStates() {
        _loginState.value = LoginState.Idle
        _registerState.value = RegisterState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: UsuarioResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}