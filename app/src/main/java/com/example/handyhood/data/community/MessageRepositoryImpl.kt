package com.example.handyhood.data.community

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.text.SimpleDateFormat
import java.util.*

class MessageRepositoryImpl(
    private val supabase: SupabaseClient
) : MessageRepository {

    private val formatter = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        Locale.US
    ).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override suspend fun getMessages(conversationId: UUID): List<Message> {
        val rows = supabase
            .from("messages")
            .select()
            .decodeList<Map<String, Any?>>()

        return rows
            .filter { row ->
                row["conversation_id"] == conversationId.toString()
            }
            .map { row ->
                Message(
                    id = UUID.fromString(row["id"] as String),
                    conversationId = conversationId,
                    senderId = UUID.fromString(row["sender_id"] as String),
                    body = row["body"] as String,
                    createdAtMillis = formatter
                        .parse(row["created_at"] as String)
                        ?.time ?: 0L
                )
            }
            .sortedBy { it.createdAtMillis }
    }

    override suspend fun sendMessage(
        conversationId: UUID,
        body: String
    ) {
        supabase.from("messages").insert(
            mapOf(
                "conversation_id" to conversationId.toString(),
                "body" to body
            )
        )
    }
}
