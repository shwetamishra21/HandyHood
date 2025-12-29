package com.example.handyhood.data.community

import java.util.UUID

data class CommunityPost(
    val id: UUID,
    val title: String,
    val body: String,
    val category: String,
    val createdBy: UUID,
    val createdAtMillis: Long,
    val isPinned: Boolean,
    val isLocked: Boolean,
    val expiresAtMillis: Long?
)
