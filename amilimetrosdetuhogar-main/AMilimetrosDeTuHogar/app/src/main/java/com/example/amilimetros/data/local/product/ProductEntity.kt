package com.example.amilimetros.data.local.product

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String? = null,
    val category: String,
    val stock: Int = 0
)
