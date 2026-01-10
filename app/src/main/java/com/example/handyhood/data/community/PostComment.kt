package com.example.handyhood.data.community

import java.util.UUID

data class PostComment(
    val id: UUID,
    val postId: UUID,
    val body: String,
    val createdBy: UUID,
    val createdAtMillis: Long
)
