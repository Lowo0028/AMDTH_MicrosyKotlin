package com.example.amilimetros.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amilimetros.data.remote.dto.FormularioAdopcionDto
import com.example.amilimetros.data.repository.FormularioApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdoptionRequestsViewModel(
    private val formularioRepository: FormularioApiRepository = FormularioApiRepository()
) : ViewModel() {

    private val _adoptionRequests = MutableStateFlow<List<FormularioAdopcionDto>>(emptyList())
    val adoptionRequests: StateFlow<List<FormularioAdopcionDto>> = _adoptionRequests

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadAdoptionRequests(usuarioId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = formularioRepository.obtenerPorUsuario(usuarioId)
            result.onSuccess { formularios ->
                _adoptionRequests.value = formularios
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error al cargar solicitudes"
            }

            _isLoading.value = false
        }
    }

    fun loadAdoptionRequests() {
        // Versión sin usuarioId - necesitarás obtenerlo del UserPreferences
        // Por ahora, muestra lista vacía
        _adoptionRequests.value = emptyList()
    }
}