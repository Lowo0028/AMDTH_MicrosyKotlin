package com.example.amilimetros.data.remote.dto

// DTO para crear una orden
data class CrearOrdenRequest(
    val usuarioId: Long,
    val total: Double,
    val items: List<ItemOrdenRequest>
)

data class ItemOrdenRequest(
    val productoId: Long,
    val productoNombre: String,
    val productoPrecio: Double,
    val cantidad: Int,
    val imageUrl: String? = null
)

// DTO para recibir una orden
data class OrdenDto(
    val id: Long,
    val usuarioId: Long,
    val total: Double,
    val createdAt: String,
    val status: String
)

// DTO para item de orden
data class ItemOrdenDto(
    val id: Long,
    val ordenId: Long,
    val productoId: Long,
    val productoNombre: String,
    val productoPrecio: Double,
    val cantidad: Int,
    val imageUrl: String? = null
)

// DTO para detalles completos de orden
data class DetallesOrdenDto(
    val orden: OrdenDto,
    val items: List<ItemOrdenDto>,
    val cantidadItems: Int
)