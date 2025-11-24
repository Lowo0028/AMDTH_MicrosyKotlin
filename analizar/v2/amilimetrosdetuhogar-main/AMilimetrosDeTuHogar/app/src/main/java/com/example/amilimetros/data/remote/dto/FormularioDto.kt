package com.example.amilimetros.data.remote.dto

data class FormularioDto(
    val id: Long? = null,
    val usuarioId: Long,
    val animalId: Long,
    val nombreSolicitante: String = "",
    val correoSolicitante: String = "",
    val telefonoSolicitante: String = "",
    val direccion: String,
    val tipoVivienda: String,
    val tieneMallasVentanas: Boolean,
    val viveEnDepartamento: Boolean,
    val tieneOtrosAnimales: Boolean,
    val motivoAdopcion: String,
    val estado: String = "PENDIENTE",
    val comentariosAdmin: String? = null,
    val fechaCreacion: String? = null,
    val fechaRevision: String? = null
)