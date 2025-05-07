package com.panashecare.assistant.model.service.dto


data class SendMessageDto (
    val to: String?, // which app is the message being sent to
    val notification: NotificationBody
)

data class NotificationBody(
    val title: String,
    val body: String
)