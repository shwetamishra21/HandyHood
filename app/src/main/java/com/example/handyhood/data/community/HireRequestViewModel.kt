package com.example.handyhood.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.handyhood.data.community.HireRequestRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import com. example. handyhood. data. community. HireRequest

class HireRequestsViewModel : ViewModel() {  // ✅ No-arg constructor

    private val repository = HireRequestRepositoryImpl()  // ✅ Internal repo
    private val _requests = MutableStateFlow<List<HireRequest>>(emptyList())
    val requests: StateFlow<List<HireRequest>> = _requests
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadRequests() = viewModelScope.launch {
        _loading.value = true
        try { _requests.value = repository.getIncomingRequests() }
        finally { _loading.value = false }
    }

    fun acceptRequest(requestId: UUID) = updateStatus(requestId, "accepted")
    fun rejectRequest(requestId: UUID) = updateStatus(requestId, "rejected")

    private fun updateStatus(requestId: UUID, status: String) = viewModelScope.launch {
        repository.updateRequestStatus(requestId, status)
        loadRequests()
    }
}
