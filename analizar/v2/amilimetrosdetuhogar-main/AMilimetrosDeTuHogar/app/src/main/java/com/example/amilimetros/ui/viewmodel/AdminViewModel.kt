package com.example.amilimetros.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amilimetros.data.remote.dto.ProductoDto
import com.example.amilimetros.data.remote.dto.AnimalDto
import com.example.amilimetros.data.repository.ProductoApiRepository
import com.example.amilimetros.data.repository.AnimalApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val productoRepository: ProductoApiRepository,
    private val animalRepository: AnimalApiRepository
) : ViewModel() {

    private val _productos = MutableStateFlow<List<ProductoDto>>(emptyList())
    val productos: StateFlow<List<ProductoDto>> = _productos

    private val _animales = MutableStateFlow<List<AnimalDto>>(emptyList())
    val animales: StateFlow<List<AnimalDto>> = _animales

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    fun cargarProductos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = productoRepository.obtenerTodos()
                if (result.isSuccess) {
                    _productos.value = result.getOrNull() ?: emptyList()
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarProducto(productoId: Long, emailAdmin: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = productoRepository.eliminar(productoId, emailAdmin)
                if (result.isSuccess) {
                    _successMessage.value = "Producto eliminado"
                    cargarProductos()
                } else {
                    _error.value = "Error al eliminar"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cargarAnimales() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = animalRepository.obtenerTodos()
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

    fun eliminarAnimal(animalId: Long, emailAdmin: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = animalRepository.eliminar(animalId, emailAdmin)
                if (result.isSuccess) {
                    _successMessage.value = "Animal eliminado"
                    cargarAnimales()
                } else {
                    _error.value = "Error al eliminar"
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