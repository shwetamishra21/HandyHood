package com.example.handyhood.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.handyhood.data.community.PostComment
import com.example.handyhood.data.community.PostCommentRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class PostCommentsViewModel(
    private val repository: PostCommentRepository
) : ViewModel() {

    private val _comments = MutableStateFlow<List<PostComment>>(emptyList())
    val comments: StateFlow<List<PostComment>> = _comments

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var pollingJob: Job? = null

    fun loadComments(postId: UUID) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _comments.value = repository.getComments(postId)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun addComment(postId: UUID, body: String) {
        viewModelScope.launch {
            repository.addComment(postId, body)
            loadComments(postId)
        }
    }

    fun deleteComment(postId: UUID, commentId: UUID) {
        viewModelScope.launch {
            repository.deleteComment(commentId)
            loadComments(postId)
        }
    }

    fun startLiveUpdates(postId: UUID) {
        if (pollingJob != null) return

        pollingJob = viewModelScope.launch {
            while (true) {
                loadComments(postId)
                delay(5_000) // refresh every 5 seconds
            }
        }
    }

    fun stopLiveUpdates() {
        pollingJob?.cancel()
        pollingJob = null
    }

    override fun onCleared() {
        stopLiveUpdates()
        super.onCleared()
    }
}
