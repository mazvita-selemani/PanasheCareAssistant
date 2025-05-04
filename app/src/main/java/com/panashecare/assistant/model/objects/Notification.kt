package com.panashecare.assistant.model.objects

enum class NotificationType{ REMINDER, WARNING, URGENT}

class Notification(
    var id: String? =null,
    val type: NotificationType? =null
) {
}