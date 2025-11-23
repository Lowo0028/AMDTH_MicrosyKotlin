package com.example.amilimetros.data.remote.dto

data class CarritoItemDto(
    val id: Long? = null,
    val usuarioId: Long,
    val productoId: Long,
    val productoNombre: String,
    val productoPrecio: Double,
    val cantidad: Int,
    val imageUrl: String? = null
)
