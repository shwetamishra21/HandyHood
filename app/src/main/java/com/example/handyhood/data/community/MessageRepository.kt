package com.example.handyhood.data.community

import java.util.UUID

interface MessageRepository {

    suspend fun getMessages(conversationId: UUID): List<Message>

    suspend fun sendMessage(
        conversationId: UUID,
        body: String
    )
}
