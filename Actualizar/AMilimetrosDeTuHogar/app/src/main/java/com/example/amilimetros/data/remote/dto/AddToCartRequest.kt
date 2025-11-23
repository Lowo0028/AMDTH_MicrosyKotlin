package com.example.amilimetros.data.remote.dto

data class AddToCartRequest(
    val usuarioId: Long,
    val productoId: Long,
    val cantidad: Int = 1
)