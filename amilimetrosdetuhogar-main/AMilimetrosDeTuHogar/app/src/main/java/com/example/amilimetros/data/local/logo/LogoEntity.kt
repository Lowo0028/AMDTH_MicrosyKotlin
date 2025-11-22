package com.example.amilimetros.data.local.logo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LogoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val image: ByteArray
)