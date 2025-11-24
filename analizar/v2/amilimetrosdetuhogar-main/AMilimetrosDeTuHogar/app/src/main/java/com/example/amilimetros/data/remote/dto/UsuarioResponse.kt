package com.example.amilimetros.data.remote.dto

data class UsuarioResponse(
    val id: Long,
    val nombre: String,
    val email: String,
    val telefono: String,
    val isAdmin: Boolean
)