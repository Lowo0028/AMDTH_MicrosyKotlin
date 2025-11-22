package com.example.amilimetros.data.local.logo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LogoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(logo: LogoEntity)

    @Query("SELECT * FROM LogoEntity LIMIT 1")
    suspend fun getLogo(): LogoEntity?
}
