package com.example.amilimetros.data.local.order

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<OrderItemEntity>)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getByOrder(orderId: Long): Flow<List<OrderItemEntity>>

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteByOrder(orderId: Long)
}