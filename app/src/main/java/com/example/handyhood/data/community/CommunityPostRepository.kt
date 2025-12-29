package com.example.handyhood.data.community

import java.util.UUID

interface CommunityPostRepository {

    suspend fun getPosts(): List<CommunityPost>

    suspend fun createPost(
        title: String,
        body: String,
        category: String,
        expiresAtMillis: Long? = null
    )

    suspend fun pinPost(postId: UUID, pinned: Boolean)

    suspend fun lockPost(postId: UUID, locked: Boolean)
}
