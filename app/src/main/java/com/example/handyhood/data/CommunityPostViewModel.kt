package com.example.handyhood.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.handyhood.data.community.CommunityPost
import com.example.handyhood.data.community.CommunityPostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class CommunityPostViewModel(
    private val repository: CommunityPostRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<CommunityPost>>(emptyList())
    val posts: StateFlow<List<CommunityPost>> = _posts

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadPosts() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _posts.value = repository.getPosts()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun createPost(
        title: String,
        body: String,
        category: String,
        expiresAtMillis: Long? = null
    ) {
        viewModelScope.launch {
            repository.createPost(
                title = title,
                body = body,
                category = category,
                expiresAtMillis = expiresAtMillis
            )
            loadPosts()
        }
    }




    fun pinPost(postId: UUID, pinned: Boolean) {
        viewModelScope.launch {
            repository.pinPost(postId, pinned)
            loadPosts()
        }
    }

    fun lockPost(postId: UUID, locked: Boolean) {
        viewModelScope.launch {
            repository.lockPost(postId, locked)
            loadPosts()
        }
    }
}
