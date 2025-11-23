package com.example.amilimetros.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amilimetros.data.remote.dto.FormularioAdopcionDto
import com.example.amilimetros.data.repository.FormularioApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FormularioViewModel(
    private val repository: FormularioApiRepository
) : ViewModel() {

    private val _formularios = MutableStateFlow<List<FormularioAdopcionDto>>(emptyList())
    val formularios: StateFlow<List<FormularioAdopcionDto>> = _formularios

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    fun cargarFormulariosUsuario(usuarioId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.obtenerPorUsuario(usuarioId)
                if (result.isSuccess) {
                    _formularios.value = result.getOrNull() ?: emptyList()
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error al cargar"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error de conexión"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun enviarFormulario(
        usuarioId: Long,
        animalId: Long,
        direccion: String,
        tipoVivienda: String,
        tieneMallasVentanas: Boolean,
        viveEnDepartamento: Boolean,
        tieneOtrosAnimales: Boolean,
        motivoAdopcion: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.crearFormulario(
                    usuarioId, animalId, direccion, tipoVivienda,
                    tieneMallasVentanas, viveEnDepartamento,
                    tieneOtrosAnimales, motivoAdopcion
                )

                if (result.isSuccess) {
                    _successMessage.value = "Formulario enviado exitosamente"
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error al enviar"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}