package com.example.amilimetros.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amilimetros.data.local.cart.CartItemEntity
import com.example.amilimetros.data.local.order.OrderEntity
import com.example.amilimetros.data.local.order.OrderItemEntity
import com.example.amilimetros.data.repository.CartRepository
import com.example.amilimetros.data.repository.OrderRepository
import com.example.amilimetros.data.repository.ProductRepository
import com.example.amilimetros.ui.notification.NotificationManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,    // ✅ NUEVO
    private val productRepository: ProductRepository, // ✅ NUEVO
    private val userId: Long
) : ViewModel() {

    // Flow de items del carrito (se actualiza automáticamente)
    val cartItems = cartRepository.getCartItems(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flow del total (se calcula automáticamente)
    val total = cartRepository.getCartTotal(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // ========== INCREMENTAR CANTIDAD ==========
    fun incrementQuantity(item: CartItemEntity) {
        viewModelScope.launch {
            cartRepository.updateQuantity(item, item.quantity + 1)
            NotificationManager.showInfo("Cantidad actualizada")
        }
    }

    // ========== DECREMENTAR CANTIDAD ==========
    fun decrementQuantity(item: CartItemEntity) {
        viewModelScope.launch {
            if (item.quantity > 1) {
                cartRepository.updateQuantity(item, item.quantity - 1)
                NotificationManager.showInfo("Cantidad actualizada")
            } else {
                cartRepository.removeFromCart(item)
                NotificationManager.showSuccess("Producto eliminado del carrito")
            }
        }
    }

    // ========== ELIMINAR ITEM ==========
    fun removeItem(item: CartItemEntity) {
        viewModelScope.launch {
            cartRepository.removeFromCart(item)
            NotificationManager.showSuccess("Producto eliminado del carrito")
        }
    }

    // ========== VACIAR CARRITO ==========
    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart(userId)
            NotificationManager.showSuccess("Carrito vaciado")
        }
    }

    // ========== PROCESAR COMPRA (NUEVO - CON HISTORIAL) ==========
    fun checkout() {
        viewModelScope.launch {
            try {
                // 1. Obtener items del carrito
                val items = cartItems.first()
                if (items.isEmpty()) {
                    NotificationManager.showWarning("El carrito está vacío")
                    return@launch
                }

                // 2. Obtener total
                val totalAmount = total.first() ?: 0.0

                // 3. Validar stock ANTES de comprar
                var stockSuficiente = true
                for (item in items) {
                    val product = productRepository.getProductById(item.productId)
                    if (product != null && product.stock < item.quantity) {
                        NotificationManager.showError("Stock insuficiente para ${product.name}")
                        stockSuficiente = false
                        break
                    }
                }

                if (!stockSuficiente) return@launch

                // 4. Crear la orden (compra)
                val order = OrderEntity(
                    userId = userId,
                    total = totalAmount,
                    status = "Completada"
                )

                // 5. Convertir cart items a order items
                val orderItems = items.map { cartItem ->
                    OrderItemEntity(
                        orderId = 0L, // Se asigna después
                        productId = cartItem.productId,
                        productName = cartItem.productName,
                        productPrice = cartItem.productPrice,
                        quantity = cartItem.quantity,
                        imageUrl = cartItem.imageUrl
                    )
                }

                // 6. Guardar orden en BD
                val result = orderRepository.createOrder(order, orderItems)

                if (result.isSuccess) {
                    // 7. Reducir stock de cada producto
                    for (item in items) {
                        val product = productRepository.getProductById(item.productId)
                        if (product != null) {
                            val updatedProduct = product.copy(
                                stock = product.stock - item.quantity
                            )
                            productRepository.updateProduct(updatedProduct)
                        }
                    }

                    // 8. Limpiar carrito
                    cartRepository.clearCart(userId)

                    // 9. Notificar éxito
                    NotificationManager.showSuccess("¡Compra realizada con éxito! Total: $$totalAmount")
                } else {
                    NotificationManager.showError("Error al procesar la compra")
                }

            } catch (e: Exception) {
                NotificationManager.showError("Error: ${e.message}")
            }
        }
    }
}