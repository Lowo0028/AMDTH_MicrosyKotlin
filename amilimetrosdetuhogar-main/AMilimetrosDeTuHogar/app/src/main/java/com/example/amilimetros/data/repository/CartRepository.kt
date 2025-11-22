package com.example.amilimetros.data.repository

import android.util.Log
import com.example.amilimetros.data.local.cart.CartDao
import com.example.amilimetros.data.local.cart.CartItemEntity
import com.example.amilimetros.data.local.product.ProductEntity
import kotlinx.coroutines.flow.Flow

class CartRepository(private val cartDao: CartDao) {

    fun getCartItems(userId: Long): Flow<List<CartItemEntity>> =
        cartDao.getCartByUserFlow(userId)

    fun getCartTotal(userId: Long): Flow<Double?> = cartDao.getTotalFlow(userId)

    suspend fun addToCart(userId: Long, product: ProductEntity, quantity: Int = 1): Result<Unit> {
        return try {
            Log.d("CartRepository", "addToCart - userId: $userId, productId: ${product.id}")

            val existing = cartDao.getItemByProduct(userId, product.id)
            Log.d("CartRepository", "Existing item: $existing")

            if (existing != null) {
                // Incrementar cantidad
                val updated = existing.copy(quantity = existing.quantity + quantity)
                Log.d("CartRepository", "Updating existing item - new quantity: ${updated.quantity}")
                cartDao.update(updated)
            } else {
                // Nuevo item
                val newItem = CartItemEntity(
                    userId = userId,
                    productId = product.id,
                    productName = product.name,
                    productPrice = product.price, // ✅ CORREGIDO
                    quantity = quantity,
                    imageUrl = product.imageUrl
                )
                Log.d("CartRepository", "Inserting new item: $newItem")
                val id = cartDao.insert(newItem)
                Log.d("CartRepository", "Item inserted with id: $id")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CartRepository", "Error adding to cart", e)
            Result.failure(e)
        }
    }

    suspend fun updateQuantity(item: CartItemEntity, newQuantity: Int): Result<Unit> {
        return try {
            if (newQuantity <= 0) {
                cartDao.delete(item)
            } else {
                cartDao.update(item.copy(quantity = newQuantity))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFromCart(item: CartItemEntity): Result<Unit> {
        return try {
            cartDao.delete(item)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearCart(userId: Long): Result<Unit> {
        return try {
            cartDao.clearCart(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
