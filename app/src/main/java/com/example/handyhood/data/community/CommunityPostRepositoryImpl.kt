package com.example.handyhood.data.community

import com.example.handyhood.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class CommunityPostRepositoryImpl : CommunityPostRepository {

    private val isoFormatter = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        Locale.US
    ).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override suspend fun getPosts(): List<CommunityPost> {
        val rows = SupabaseClient.client
            .from("community_posts")
            .select()
            .decodeList<JsonObject>()

        return rows.mapNotNull { row ->
            try {
                CommunityPost(
                    id = UUID.fromString(row["id"]?.jsonPrimitive?.content ?: return@mapNotNull null),
                    title = row["title"]?.jsonPrimitive?.content ?: "",
                    body = row["body"]?.jsonPrimitive?.content ?: "",
                    category = row["category"]?.jsonPrimitive?.content ?: "",
                    createdBy = UUID.fromString(row["created_by"]?.jsonPrimitive?.content ?: return@mapNotNull null),
                    createdAtMillis = parseIsoMillis(row["created_at"]?.jsonPrimitive?.content ?: ""),
                    isPinned = row["is_pinned"]?.jsonPrimitive?.content?.toBoolean() ?: false,
                    isLocked = row["is_locked"]?.jsonPrimitive?.content?.toBoolean() ?: false,
                    expiresAtMillis = row["expires_at"]?.jsonPrimitive?.content?.let { parseIsoMillis(it) }
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun createPost(title: String, body: String, category: String, expiresAtMillis: Long?) {
        SupabaseClient.client.from("community_posts").insert(
            mapOf(
                "title" to title,
                "body" to body,
                "category" to category,
                "expires_at" to expiresAtMillis?.let { isoFormatter.format(it) }
            )
        )
    }

    override suspend fun pinPost(postId: UUID, pinned: Boolean) {
        SupabaseClient.client.from("community_posts")
            .update(mapOf("is_pinned" to pinned)) {
                filter { eq("id", postId.toString()) }
            }
    }

    override suspend fun lockPost(postId: UUID, locked: Boolean) {
        SupabaseClient.client.from("community_posts")
            .update(mapOf("is_locked" to locked)) {
                filter { eq("id", postId.toString()) }
            }
    }

    private fun parseIsoMillis(iso: String): Long {
        return isoFormatter.parse(iso)?.time ?: 0L
    }
}
