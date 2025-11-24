package com.example.amilimetros.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amilimetros.data.local.storage.UserPreferences
import com.example.amilimetros.data.remote.dto.OrdenDto
import com.example.amilimetros.data.remote.dto.ItemOrdenDto
import com.example.amilimetros.data.repository.OrdenApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// DTOs para la UI (compatibles con lo que ya tienes)
data class OrderDto(
    val id: Long,
    val fecha: String,
    val total: Double,
    val estado: String,
    val items: List<OrderItemDto>
)

data class OrderItemDto(
    val productoNombre: String,
    val cantidad: Int,
    val precio: Double
)

class OrderHistoryViewModel(
    private val userPreferences: UserPreferences,
    private val ordenRepository: OrdenApiRepository
) : ViewModel() {

    private val _orders = MutableStateFlow<List<OrderDto>>(emptyList())
    val orders: StateFlow<List<OrderDto>> = _orders

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadOrderHistory()
    }

    fun loadOrderHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val userId = userPreferences.getUserId()

                if (userId == null) {
                    _errorMessage.value = "Usuario no autenticado"
                    _orders.value = emptyList()
                    return@launch
                }

                // 🔥 LLAMADA REAL A LA API
                val result = ordenRepository.obtenerOrdenesPorUsuario(userId)

                if (result.isSuccess) {
                    val ordenes = result.getOrNull() ?: emptyList()

                    // Convertir cada orden y obtener sus items
                    val ordenesConItems = ordenes.map { orden ->
                        val itemsResult = ordenRepository.obtenerItemsDeOrden(orden.id)
                        val items = if (itemsResult.isSuccess) {
                            itemsResult.getOrNull()?.map { item ->
                                OrderItemDto(
                                    productoNombre = item.productoNombre,
                                    cantidad = item.cantidad,
                                    precio = item.productoPrecio
                                )
                            } ?: emptyList()
                        } else {
                            emptyList()
                        }

                        OrderDto(
                            id = orden.id,
                            fecha = formatearFecha(orden.createdAt),
                            total = orden.total,
                            estado = orden.status,
                            items = items
                        )
                    }

                    _orders.value = ordenesConItems
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message
                        ?: "Error al cargar historial"
                }

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al cargar historial"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun formatearFecha(fechaISO: String): String {
        return try {
            val dateTime = LocalDateTime.parse(fechaISO)
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            dateTime.format(formatter)
        } catch (e: Exception) {
            fechaISO // Si falla, devolver la fecha original
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}