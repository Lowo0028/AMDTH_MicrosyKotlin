import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class OrderDto(
    val id: Long,
    val fecha: String,
    val total: Double,
    val estado: String,
    val items: List<String>
)

class OrderHistoryViewModel : ViewModel() {

    private val _orders = MutableStateFlow<List<OrderDto>>(emptyList())
    val orders: StateFlow<List<OrderDto>> = _orders

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadOrderHistory(usuarioId: Long? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {

                _orders.value = emptyList()

                // }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al cargar historial"
            }

            _isLoading.value = false
        }
    }
}