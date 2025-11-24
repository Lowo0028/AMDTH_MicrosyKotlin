package com.example.amilimetros.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amilimetros.data.remote.dto.AddToCartRequest
import com.example.amilimetros.data.remote.dto.CarritoItemDto
import com.example.amilimetros.data.remote.dto.CrearOrdenRequest
import com.example.amilimetros.data.remote.dto.ItemOrdenRequest
import com.example.amilimetros.data.repository.CarritoApiRepository
import com.example.amilimetros.data.repository.OrdenApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(
    private val carritoRepository: CarritoApiRepository,
    private val ordenRepository: OrdenApiRepository
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

    private val _checkoutSuccess = MutableStateFlow(false)
    val checkoutSuccess: StateFlow<Boolean> = _checkoutSuccess

    fun cargarCarrito(usuarioId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = carritoRepository.obtenerCarrito(usuarioId)
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
                val result = carritoRepository.calcularTotal(usuarioId)
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
                val result = carritoRepository.agregarAlCarrito(request)
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
                val result = carritoRepository.actualizarCantidad(itemId, nuevaCantidad)
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
                val result = carritoRepository.eliminarItem(itemId)
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
                val result = carritoRepository.vaciarCarrito(usuarioId)
                if (result.isSuccess) {
                    _successMessage.value = "Carrito vaciado"
                    cargarCarrito(usuarioId)
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // 🔥 NUEVA FUNCIÓN: Proceder al checkout
    fun procederACompra(usuarioId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _checkoutSuccess.value = false

            try {
                if (_items.value.isEmpty()) {
                    _error.value = "El carrito está vacío"
                    _isLoading.value = false
                    return@launch
                }

                // Convertir items del carrito a items de orden
                val itemsOrden = _items.value.map { item ->
                    ItemOrdenRequest(
                        productoId = item.productoId,
                        productoNombre = item.productoNombre,
                        productoPrecio = item.productoPrecio,
                        cantidad = item.cantidad,
                        imageUrl = item.imageUrl
                    )
                }

                // Crear la orden
                val request = CrearOrdenRequest(
                    usuarioId = usuarioId,
                    total = _total.value,
                    items = itemsOrden
                )

                val result = ordenRepository.crearOrden(request)

                if (result.isSuccess) {
                    // Vaciar el carrito después de crear la orden
                    carritoRepository.vaciarCarrito(usuarioId)

                    _successMessage.value = "¡Compra realizada exitosamente!"
                    _checkoutSuccess.value = true
                    _items.value = emptyList()
                    _total.value = 0.0
                } else {
                    _error.value = result.exceptionOrNull()?.message
                        ?: "Error al procesar la compra"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al procesar la compra"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetCheckoutSuccess() {
        _checkoutSuccess.value = false
    }

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}