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

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    // ✅ Day 8.4 — last successful refresh timestamp
    private val _lastUpdated = MutableStateFlow<Long?>(null)
    val lastUpdated: StateFlow<Long?> = _lastUpdated

    init {
        refresh()

        // ✅ Realtime hook (already compiled in your repo)
        RequestRepository.startRequestsRealtime {
            refresh()
        }
    }

    fun refresh() {
        if (_isRefreshing.value) return

        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                _requests.value = RequestRepository.fetchRequests()
                _error.value = null
                _lastUpdated.value = System.currentTimeMillis()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load requests"
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun updateDate(id: String, date: String) {
        viewModelScope.launch {
            RequestRepository.updateRequestDate(id, date)
            refresh()
        }
    }

    fun updateDetails(id: String, title: String, description: String) {
        viewModelScope.launch {
            RequestRepository.updateRequestDetails(id, title, description)
            refresh()
        }
    }

    fun cancel(id: String) {
        viewModelScope.launch {
            RequestRepository.cancelRequest(id)
            refresh()
        }
    }
}
