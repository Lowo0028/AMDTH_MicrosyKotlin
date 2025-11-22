package com.example.amilimetros.data.repository

import com.example.amilimetros.data.local.order.OrderDao
import com.example.amilimetros.data.local.order.OrderEntity
import com.example.amilimetros.data.local.order.OrderItemDao
import com.example.amilimetros.data.local.order.OrderItemEntity
import kotlinx.coroutines.flow.Flow

class OrderRepository(
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao
) {

    fun getUserOrders(userId: Long): Flow<List<OrderEntity>> =
        orderDao.getByUser(userId)

    fun getAllOrders(): Flow<List<OrderEntity>> =
        orderDao.getAll()

    fun getOrderItems(orderId: Long): Flow<List<OrderItemEntity>> =
        orderItemDao.getByOrder(orderId)

    suspend fun createOrder(order: OrderEntity, items: List<OrderItemEntity>): Result<Long> {
        return try {
            val orderId = orderDao.insert(order)
            val itemsWithOrderId = items.map { it.copy(orderId = orderId) }
            orderItemDao.insertAll(itemsWithOrderId)
            Result.success(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrderById(orderId: Long): OrderEntity? =
        orderDao.getById(orderId)
}