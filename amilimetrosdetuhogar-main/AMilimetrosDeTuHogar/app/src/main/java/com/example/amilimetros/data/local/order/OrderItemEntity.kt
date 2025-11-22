package com.example.amilimetros.data.local.order

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val orderId: Long,
    val productId: Long,
    val productName: String,
    val productPrice: Double,
    val quantity: Int,
    val imageUrl: String? = null
)