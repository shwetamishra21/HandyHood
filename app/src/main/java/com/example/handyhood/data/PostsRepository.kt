@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.example.handyhood.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

/**
 * PostsRepository - placeholder implementation (Supabase removed).
 *
 * TODO: Replace the internals with actual backend calls (e.g. Firestore, REST API, Hasura).
 * For now these functions are no-ops / return empty lists so the app builds cleanly.
 */

@Serializable
data class Post(
    val id: String? = null,
    val title: String,
    val author: String,
    val content: String,
    val timestamp: Long
)

object PostsRepository {

    /**
     * Fetch posts from backend.
     * Current stub: returns empty list.
     * Replace by calling your backend and mapping results to [Post].
     */
    suspend fun fetchPosts(): List<Post> = withContext(Dispatchers.IO) {
        try {
            // TODO: Implement: query your backend -> map to Post list
            emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Add a new post to backend.
     * Current stub: no-op.
     * Replace with an insert call to your backend.
     */
    suspend fun addPost(title: String, author: String, content: String) {
        withContext(Dispatchers.IO) {
            try {
                val newPost = Post(
                    title = title,
                    author = author,
                    content = content,
                    timestamp = System.currentTimeMillis()
                )

                // TODO: Implement: send `newPost` to backend (Firestore/REST/etc)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
