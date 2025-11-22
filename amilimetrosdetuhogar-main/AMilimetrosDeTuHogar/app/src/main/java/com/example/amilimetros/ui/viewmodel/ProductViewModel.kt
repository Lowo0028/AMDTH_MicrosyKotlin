package com.example.amilimetros.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amilimetros.data.local.product.ProductEntity
import com.example.amilimetros.data.repository.ProductRepository
import com.example.amilimetros.data.repository.CartRepository
import com.example.amilimetros.ui.notification.NotificationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProductUiState(
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    val successMsg: String? = null
)

class ProductViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState

    // Lista de productos (Flow que se actualiza automáticamente)
    val products = productRepository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ========== AGREGAR AL CARRITO ==========
    fun addToCart(userId: Long, product: ProductEntity) {
        Log.d("ProductViewModel", "addToCart called - userId: $userId, product: ${product.name}")

        if (userId == 0L) {
            Log.w("ProductViewModel", "User not logged in")
            NotificationManager.showWarning("Debes iniciar sesión para agregar al carrito")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            Log.d("ProductViewModel", "Calling cartRepository.addToCart...")
            val result = cartRepository.addToCart(userId, product, 1)

            _uiState.value = if (result.isSuccess) {
                Log.d("ProductViewModel", "Product added successfully")
                NotificationManager.showSuccess("${product.name} agregado al carrito")
                _uiState.value.copy(
                    isLoading = false,
                    successMsg = "${product.name} agregado al carrito",
                    errorMsg = null
                )
            } else {
                Log.e("ProductViewModel", "Error adding product: ${result.exceptionOrNull()?.message}")
                NotificationManager.showError("Error al agregar al carrito")
                _uiState.value.copy(
                    isLoading = false,
                    errorMsg = "Error al agregar al carrito",
                    successMsg = null
                )
            }
        }
    }

    // ========== ADMIN: AGREGAR PRODUCTO ==========
    fun addProduct(product: ProductEntity) {
        viewModelScope.launch {
            val result = productRepository.addProduct(product)
            if (result.isSuccess) {
                NotificationManager.showSuccess("Producto agregado correctamente")
                _uiState.value = _uiState.value.copy(successMsg = "Producto agregado", errorMsg = null)
            } else {
                NotificationManager.showError("Error al agregar producto")
                _uiState.value = _uiState.value.copy(errorMsg = "Error al agregar producto", successMsg = null)
            }
        }
    }

    // ========== ADMIN: ACTUALIZAR PRODUCTO ==========
    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            val result = productRepository.updateProduct(product)
            if (result.isSuccess) {
                NotificationManager.showSuccess("Producto actualizado correctamente")
                _uiState.value = _uiState.value.copy(successMsg = "Producto actualizado", errorMsg = null)
            } else {
                NotificationManager.showError("Error al actualizar producto")
                _uiState.value = _uiState.value.copy(errorMsg = "Error al actualizar", successMsg = null)
            }
        }
    }

    // ========== ADMIN: ELIMINAR PRODUCTO ==========
    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            val result = productRepository.deleteProduct(product)
            if (result.isSuccess) {
                NotificationManager.showSuccess("Producto eliminado correctamente")
                _uiState.value = _uiState.value.copy(successMsg = "Producto eliminado", errorMsg = null)
            } else {
                NotificationManager.showError("Error al eliminar producto")
                _uiState.value = _uiState.value.copy(errorMsg = "Error al eliminar", successMsg = null)
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(successMsg = null, errorMsg = null)
    }
}