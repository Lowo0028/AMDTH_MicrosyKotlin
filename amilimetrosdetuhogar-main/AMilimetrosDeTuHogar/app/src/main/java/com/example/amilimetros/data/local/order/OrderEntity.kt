package com.example.amilimetros.data.local.order

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long,
    val total: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "Completada" // Completada, Cancelada, Pendiente
)