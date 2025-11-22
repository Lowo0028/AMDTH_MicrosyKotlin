package com.example.amilimetros.data.local.adoption

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "adoption_forms")
data class AdoptionFormEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long,
    val animalId: Long,
    val animalName: String,
    val userName: String,
    val userEmail: String,
    val userPhone: String,
    val reason: String,
    val hasBalconyNets: Boolean,
    val livesInApartment: Boolean,
    val photo: ByteArray? = null,
    val submittedAt: Long = System.currentTimeMillis(),
    val status: String = "Pendiente" // Pendiente, Aprobado, Rechazado
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AdoptionFormEntity
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int = id.hashCode()
}