@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.example.handyhood.data

import com.example.handyhood.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer

@Serializable
data class Post(
    val id: String? = null,
    val title: String,
    val author: String,
    val content: String,
    val timestamp: Long
)

interface PostsRepository {
    suspend fun fetchPosts(): List<Post>
    suspend fun addPost(title: String, author: String, content: String)  // ✅ Unit return
}

class PostsRepositoryImpl : PostsRepository {

    override suspend fun fetchPosts(): List<Post> = withContext(Dispatchers.IO) {
        try {
            val result = SupabaseClient.client
                .from("posts")
                .select()
                .decodeList<Post>()  // ✅ Direct decodeList<Post>
            result
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addPost(title: String, author: String, content: String): Unit =
        withContext(Dispatchers.IO) {  // ✅ Explicit Unit return
            try {
                val newPost = Post(
                    title = title,
                    author = author,
                    content = content,
                    timestamp = System.currentTimeMillis()
                )
                SupabaseClient.client.from("posts").insert(newPost)
                // ✅ No return value = Unit
            } catch (e: Exception) {
                // Silent fail
            }
        }
}
