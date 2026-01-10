package com.example.handyhood.data.community

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class PostCommentRepositoryImpl(
    private val supabase: SupabaseClient
) : PostCommentRepository {

    private val isoFormatter = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        Locale.US
    ).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override suspend fun getComments(postId: UUID): List<PostComment> {
        val rows = supabase
            .from("post_comments")
            .select()
            .decodeList<Map<String, Any?>>()

        return rows
            .filter {
                it["post_id"] == postId.toString() &&
                        it["is_deleted"] == false
            }
            .sortedBy {
                parseIsoMillis(it["created_at"] as String)
            }
            .map { row ->
                PostComment(
                    id = UUID.fromString(row["id"] as String),
                    postId = UUID.fromString(row["post_id"] as String),
                    body = row["body"] as String,
                    createdBy = UUID.fromString(row["created_by"] as String),
                    createdAtMillis = parseIsoMillis(row["created_at"] as String)
                )
            }
    }

    override suspend fun addComment(
        postId: UUID,
        body: String
    ) {
        supabase.from("post_comments").insert(
            mapOf(
                "post_id" to postId.toString(),
                "body" to body
            )
        )
    }

    override suspend fun deleteComment(commentId: UUID) {
        supabase.from("post_comments")
            .update(mapOf("is_deleted" to true)) {
                filter {
                    eq("id", commentId.toString())
                }
            }
    }

    private fun parseIsoMillis(iso: String): Long {
        return isoFormatter.parse(iso)?.time ?: 0L
    }
}
