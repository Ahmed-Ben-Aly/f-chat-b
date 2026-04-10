package com.ahmed.familychat.model

data class User(
    val id: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val status: String = "offline"
)

data class Message(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String? = null,
    val content: String? = null,
    val type: String = "text", // "text", "voice", "call_log"
    val mediaUrl: String? = null,
    val duration: Int = 0,
    val createdAt: String = "",
    val isRead: Boolean = false
)
