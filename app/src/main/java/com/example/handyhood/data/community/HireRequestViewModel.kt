package com.example.handyhood.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.handyhood.data.community.HireRequest
import com.example.handyhood.data.community.HireRequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class HireRequestsViewModel(
    private val repository: HireRequestRepository
) : ViewModel() {

    private val _requests = MutableStateFlow<List<HireRequest>>(emptyList())
    val requests: StateFlow<List<HireRequest>> = _requests

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadRequests() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _requests.value = repository.getIncomingRequests()
            } finally {
                _loading.value = false
            }
        }
    }

    fun acceptRequest(requestId: UUID) {
        updateStatus(requestId, "accepted")
    }

    fun rejectRequest(requestId: UUID) {
        updateStatus(requestId, "rejected")
    }

    private fun updateStatus(requestId: UUID, status: String) {
        viewModelScope.launch {
            repository.updateRequestStatus(requestId, status)
            loadRequests()
        }
    }
}
