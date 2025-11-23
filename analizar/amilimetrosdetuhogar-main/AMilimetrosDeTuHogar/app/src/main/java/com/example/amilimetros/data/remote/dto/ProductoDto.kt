package com.example.amilimetros.data.remote.dto

data class ProductoDto(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val categoria: String,
    val imagen: ByteArray? = null
)
