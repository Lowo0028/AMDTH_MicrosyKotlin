package com.example.amilimetros.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amilimetros.data.remote.dto.ProductoDto
import com.example.amilimetros.data.repository.ProductoApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel(
    private val repository: ProductoApiRepository
) : ViewModel() {

    private val _productos = MutableStateFlow<List<ProductoDto>>(emptyList())
    val productos: StateFlow<List<ProductoDto>> = _productos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val result = repository.getProductos()
                if (result.isSuccess) {
                    _productos.value = result.getOrNull() ?: emptyList()
                } else {
                    _error.value = "Error al cargar productos"
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar productos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun buscarProductos(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Filtrar localmente
                _productos.value = _productos.value.filter {
                    it.nombre.contains(query, ignoreCase = true)
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