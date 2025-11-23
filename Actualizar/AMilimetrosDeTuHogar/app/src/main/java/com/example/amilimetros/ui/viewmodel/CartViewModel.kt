package com.example.amilimetros.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amilimetros.data.remote.dto.AddToCartRequest
import com.example.amilimetros.data.remote.dto.CarritoItemDto
import com.example.amilimetros.data.repository.CarritoApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(
    private val repository: CarritoApiRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<CarritoItemDto>>(emptyList())
    val items: StateFlow<List<CarritoItemDto>> = _items

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    fun cargarCarrito(usuarioId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.obtenerCarrito(usuarioId)
                if (result.isSuccess) {
                    _items.value = result.getOrNull() ?: emptyList()
                    calcularTotal(usuarioId)
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error al cargar carrito"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error de conexión"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun calcularTotal(usuarioId: Long) {
        viewModelScope.launch {
            try {
                val result = repository.calcularTotal(usuarioId)
                if (result.isSuccess) {
                    _total.value = result.getOrNull() ?: 0.0
                }
            } catch (e: Exception) {
                // Error silencioso
            }
        }
    }

    fun agregarAlCarrito(usuarioId: Long, productoId: Long, cantidad: Int = 1) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = AddToCartRequest(usuarioId, productoId, cantidad)
                val result = repository.agregarAlCarrito(request)
                if (result.isSuccess) {
                    _successMessage.value = "Producto agregado al carrito"
                    cargarCarrito(usuarioId)
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error al agregar"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarCantidad(itemId: Long?, nuevaCantidad: Int, usuarioId: Long) {
        viewModelScope.launch {
            try {
                val result = repository.actualizarCantidad(itemId, nuevaCantidad)
                if (result.isSuccess) {
                    cargarCarrito(usuarioId)
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun eliminarItem(itemId: Long?, usuarioId: Long) {
        viewModelScope.launch {
            try {
                val result = repository.eliminarItem(itemId)
                if (result.isSuccess) {
                    _successMessage.value = "Producto eliminado"
                    cargarCarrito(usuarioId)
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun vaciarCarrito(usuarioId: Long) {
        viewModelScope.launch {
            try {
                val result = repository.vaciarCarrito(usuarioId)
                if (result.isSuccess) {
                    _successMessage.value = "Carrito vaciado"
                    cargarCarrito(usuarioId)
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}