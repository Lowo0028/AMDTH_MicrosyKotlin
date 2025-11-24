
package com.example.amilimetros.data.remote.dto

data class RegisterRequest(
    val nombre: String,
    val email: String,
    val telefono: String,
    val contrasena: String
)