package com.example.amilimetros.data.local.animal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val species: String, // Ej: "Perro", "Gato"
    val breed: String,
    val age: Int,
    val description: String,
    val imageUrl: String? = null,
    val isAdopted: Boolean = false
)