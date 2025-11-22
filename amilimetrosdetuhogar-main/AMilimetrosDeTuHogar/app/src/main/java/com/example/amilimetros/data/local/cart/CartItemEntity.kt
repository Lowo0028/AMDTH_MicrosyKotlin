package com.example.amilimetros.data.local.cart

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long, // Usuario dueño del carrito
    val productId: Long,
    val productName: String,
    val productPrice: Double,
    val quantity: Int,
    val imageUrl: String? = null
)