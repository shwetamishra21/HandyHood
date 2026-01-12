package com.example.handyhood.data.community

import java.util.UUID

data class Conversation(
    val id: UUID,
    val hireRequestId: UUID,
    val lastMessage: String?,
    val lastMessageAtMillis: Long?
)
