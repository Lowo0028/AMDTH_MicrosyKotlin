package com.example.amilimetros.ui.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NotificationState(
    val message: String = "",
    val type: NotificationType = NotificationType.INFO,
    val isVisible: Boolean = false
)

enum class NotificationType {
    SUCCESS,
    ERROR,
    INFO,
    WARNING
}

object NotificationManager {
    private val _notificationState = MutableStateFlow(NotificationState())
    val notificationState: StateFlow<NotificationState> = _notificationState.asStateFlow()

    fun showSuccess(message: String) {
        show(message, NotificationType.SUCCESS)
    }

    fun showError(message: String) {
        show(message, NotificationType.ERROR)
    }

    fun showInfo(message: String) {
        show(message, NotificationType.INFO)
    }

    fun showWarning(message: String) {
        show(message, NotificationType.WARNING)
    }

    private fun show(message: String, type: NotificationType) {
        _notificationState.value = NotificationState(
            message = message,
            type = type,
            isVisible = true
        )
    }

    fun dismiss() {
        _notificationState.value = _notificationState.value.copy(isVisible = false)
    }
}