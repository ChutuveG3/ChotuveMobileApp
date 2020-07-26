package com.example.chotuvemobileapp.entities

import java.time.LocalDateTime

class ChatItem (
    var messagesId: String = "",
    var user: String = "",
    var lastMessage: String = "",
    var timestamp: Long = 0,
    var picUrl: String = ""
)