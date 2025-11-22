package com.example.amilimetros.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amilimetros.data.local.order.OrderItemEntity
import com.example.amilimetros.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val userId: Long
) : ViewModel() {

    val orders = orderRepository.getUserOrders(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedOrderItems = MutableStateFlow<List<OrderItemEntity>>(emptyList())
    val selectedOrderItems: StateFlow<List<OrderItemEntity>> = _selectedOrderItems

    private val _showDetailsDialog = MutableStateFlow(false)
    val showDetailsDialog: StateFlow<Boolean> = _showDetailsDialog

    fun loadOrderItems(orderId: Long) {
        viewModelScope.launch {
            orderRepository.getOrderItems(orderId).collect { items ->
                _selectedOrderItems.value = items
                _showDetailsDialog.value = true
            }
        }
    }

    fun closeDetailsDialog() {
        _showDetailsDialog.value = false
    }
}