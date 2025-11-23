package com.example.amilimetros.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amilimetros.data.local.storage.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// DTO para representar una orden/compra
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
    private val userPreferences: UserPreferences
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

                // ⚠️ DATOS SIMULADOS - Reemplazar cuando tengas el microservicio de órdenes
                // Por ahora mostramos un historial de ejemplo
                val ordenesDePrueba = generarOrdenesDePrueba(userId)
                _orders.value = ordenesDePrueba

                // 🔧 TODO: Cuando implementes el microservicio de órdenes, reemplaza con:
                // val result = ordenRepository.obtenerOrdenesPorUsuario(userId)
                // if (result.isSuccess) {
                //     _orders.value = result.getOrNull() ?: emptyList()
                // } else {
                //     _errorMessage.value = "Error al cargar historial"
                // }

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al cargar historial"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ⚠️ FUNCIÓN TEMPORAL - Genera órdenes de prueba
    private fun generarOrdenesDePrueba(userId: Long): List<OrderDto> {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()

        return listOf(
            OrderDto(
                id = 1001,
                fecha = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, -5) }.time),
                total = 45980.0,
                estado = "ENTREGADO",
                items = listOf(
                    OrderItemDto("Alimento Premium Perro", 1, 35990.0),
                    OrderItemDto("Pelota Interactiva", 1, 8990.0)
                )
            ),
            OrderDto(
                id = 1002,
                fecha = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, -10) }.time),
                total = 32980.0,
                estado = "ENTREGADO",
                items = listOf(
                    OrderItemDto("Rascador para Gatos", 1, 25990.0),
                    OrderItemDto("Collar Ajustable", 1, 6990.0)
                )
            ),
            OrderDto(
                id = 1003,
                fecha = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, -15) }.time),
                total = 12990.0,
                estado = "ENTREGADO",
                items = listOf(
                    OrderItemDto("Arena para Gatos", 1, 12990.0)
                )
            )
        )
    }

    fun clearError() {
        _errorMessage.value = null
    }
}