package com.example.chotuvemobileapp.entities

import java.time.LocalDateTime

class ChatItem (
    val messagesId: String,
    val user: String,
    val lastMessage: String,
    val timestamp: LocalDateTime
)