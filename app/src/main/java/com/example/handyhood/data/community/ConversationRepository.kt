package com.example.handyhood.data.community

interface ConversationRepository {
    suspend fun getMyConversations(): List<Conversation>
}
