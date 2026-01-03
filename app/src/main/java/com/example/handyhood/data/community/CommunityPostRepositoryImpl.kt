package com.example.handyhood.data.community

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.UUID


class CommunityPostRepositoryImpl(
    private val supabase: SupabaseClient
) : CommunityPostRepository {

    private val isoFormatter = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        Locale.US
    ).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override suspend fun getPosts(): List<CommunityPost> {
        val rows = supabase
            .from("community_posts")
            .select()
            .decodeList<Map<String, Any?>>()

        return rows.map { row ->
            CommunityPost(
                id = UUID.fromString(row["id"] as String),
                title = row["title"] as String,
                body = row["body"] as String,
                category = row["category"] as String,
                createdBy = UUID.fromString(row["created_by"] as String),
                createdAtMillis = parseIsoMillis(row["created_at"] as String),
                isPinned = row["is_pinned"] as Boolean,
                isLocked = row["is_locked"] as Boolean,
                expiresAtMillis = (row["expires_at"] as String?)?.let {
                    parseIsoMillis(it)
                }
            )
        }
    }

    override suspend fun createPost(
        title: String,
        body: String,
        category: String,
        expiresAtMillis: Long?
    ) {
        supabase.from("community_posts").insert(
            mapOf(
                "title" to title,
                "body" to body,
                "category" to category,
                "expires_at" to expiresAtMillis?.let {
                    isoFormatter.format(it)
                }
            )
        )
    }

    override suspend fun pinPost(postId: UUID, pinned: Boolean) {
        supabase.from("community_posts")
            .update(mapOf("is_pinned" to pinned)) {
                filter { eq("id", postId.toString()) }
            }
    }

    override suspend fun lockPost(postId: UUID, locked: Boolean) {
        supabase.from("community_posts")
            .update(mapOf("is_locked" to locked)) {
                filter { eq("id", postId.toString()) }
            }
    }



    private fun parseIsoMillis(iso: String): Long {
        return isoFormatter.parse(iso)?.time ?: 0L
    }
}
