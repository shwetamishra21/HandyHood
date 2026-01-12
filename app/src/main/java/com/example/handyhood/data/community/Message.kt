package com.example.handyhood.data.community

import java.util.UUID

data class Message(
    val id: UUID,
    val conversationId: UUID,
    val senderId: UUID,
    val body: String,
    val createdAtMillis: Long
)
