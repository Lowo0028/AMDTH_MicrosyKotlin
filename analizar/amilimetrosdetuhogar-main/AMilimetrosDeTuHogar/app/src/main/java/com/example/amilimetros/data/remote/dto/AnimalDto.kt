package com.example.amilimetros.data.remote.dto

data class AnimalDto(
    val id: Long,
    val nombre: String,
    val especie: String,
    val raza: String,
    val edad: String,
    val descripcion: String,
    val isAdoptado: Boolean,
    val imagen: ByteArray? = null
)
