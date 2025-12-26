package com.example.handyhood.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RequestsViewModel : ViewModel() {

    private val _requests =
        MutableStateFlow<List<Map<String, Any?>>>(emptyList())
    val requests: StateFlow<List<Map<String, Any?>>> = _requests

    private val _error =
        MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadRequests()
    }

    fun loadRequests() {
        viewModelScope.launch {
            try {
                _requests.value = RequestRepository.fetchRequests()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load requests"
            }
        }
    }
}
