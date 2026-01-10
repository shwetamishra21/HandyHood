package com.example.handyhood.data.community

import java.util.UUID

interface PostCommentRepository {

    suspend fun getComments(postId: UUID): List<PostComment>

    suspend fun addComment(
        postId: UUID,
        body: String
    )

    suspend fun deleteComment(commentId: UUID)
}
