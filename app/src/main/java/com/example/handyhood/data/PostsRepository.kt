@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.example.handyhood.data

import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import com.example.handyhood.data.remote.SupabaseClient
import kotlinx.serialization.builtins.ListSerializer

@Serializable
data class Post(
    val id: String? = null,
    val title: String,
    val author: String,
    val content: String,
    val timestamp: Long
)

object PostsRepository {

    suspend fun fetchPosts(): List<Post> = withContext(Dispatchers.IO) {
        try {
            val result = SupabaseClient.client
                .from("posts")
                .select()

            Json.decodeFromString(
                ListSerializer(Post.serializer()),
                result.data ?: "[]"
            )

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun addPost(title: String, author: String, content: String) {
        withContext(Dispatchers.IO) {
            try {
                val newPost = Post(
                    title = title,
                    author = author,
                    content = content,
                    timestamp = System.currentTimeMillis()
                )

                SupabaseClient.client
                    .from("posts")
                    .insert(newPost)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
