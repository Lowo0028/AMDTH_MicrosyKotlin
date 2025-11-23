package com.example.amilimetros.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amilimetros.data.remote.dto.AnimalDto
import com.example.amilimetros.data.repository.AnimalApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnimalViewModel(
    private val repository: AnimalApiRepository
) : ViewModel() {

    private val _animales = MutableStateFlow<List<AnimalDto>>(emptyList())
    val animales: StateFlow<List<AnimalDto>> = _animales

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        cargarAnimalesDisponibles()
    }

    fun cargarAnimalesDisponibles() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.obtenerDisponibles()
                if (result.isSuccess) {
                    _animales.value = result.getOrNull() ?: emptyList()
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error al cargar animales"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error de conexión"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun buscarAnimales(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.buscarPorNombre(query)
                if (result.isSuccess) {
                    _animales.value = result.getOrNull() ?: emptyList()
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filtrarPorEspecie(especie: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.obtenerPorEspecie(especie)
                if (result.isSuccess) {
                    _animales.value = result.getOrNull() ?: emptyList()
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}