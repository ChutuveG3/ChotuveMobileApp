package com.example.chotuvemobileapp.entities
import java.time.LocalDateTime

class ChatMessageItem (
    val user: String,
    val message: String,
    val timestamp: LocalDateTime
)