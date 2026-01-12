package com.example.handyhood.data.community

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.text.SimpleDateFormat
import java.util.*

class ConversationRepositoryImpl(
    private val supabase: SupabaseClient
) : ConversationRepository {

    private val formatter = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        Locale.US
    ).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override suspend fun getMyConversations(): List<Conversation> {
        val conversations = supabase
            .from("conversations")
            .select()
            .decodeList<Map<String, Any?>>()

        if (conversations.isEmpty()) return emptyList()

        val messages = supabase
            .from("messages")
            .select()
            .decodeList<Map<String, Any?>>()
            .groupBy { it["conversation_id"] as String }

        return conversations.map { row ->
            val convoId = UUID.fromString(row["id"] as String)

            val lastMessageRow = messages[convoId.toString()]
                ?.maxByOrNull {
                    formatter.parse(it["created_at"] as String)?.time ?: 0L
                }

            Conversation(
                id = convoId,
                hireRequestId = UUID.fromString(row["hire_request_id"] as String),
                lastMessage = lastMessageRow?.get("body") as String?,
                lastMessageAtMillis = lastMessageRow?.get("created_at")?.let {
                    formatter.parse(it as String)?.time
                }
            )
        }
    }
}
