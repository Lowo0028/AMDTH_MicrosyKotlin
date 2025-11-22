package com.example.amilimetros.data.local.cart

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItemEntity): Long

    @Update
    suspend fun update(item: CartItemEntity)

    @Delete
    suspend fun delete(item: CartItemEntity)

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    fun getCartByUserFlow(userId: Long): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId")
    suspend fun getItemByProduct(userId: Long, productId: Long): CartItemEntity?

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: Long)

    // ✅ CORREGIDO: debe ser productPrice * quantity
    @Query("SELECT SUM(productPrice * quantity) FROM cart_items WHERE userId = :userId")
    fun getTotalFlow(userId: Long): Flow<Double?>
}