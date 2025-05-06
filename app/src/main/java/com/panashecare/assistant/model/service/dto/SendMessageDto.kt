package com.panashecare.assistant.model.service.dto


data class SendMessageDto (
    val to: String?, // which app is teh message being sent to
    val notification: NotificationBody
)

data class NotificationBody(
    val title: String,
    val body: String
)