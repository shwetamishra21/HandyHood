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

    // ✅ Day 8.4
    private val _lastUpdated = MutableStateFlow<Long?>(null)
    val lastUpdated: StateFlow<Long?> = _lastUpdated

    // ✅ Day 11.4.1 — MUTATION GUARD
    private val _isMutating = MutableStateFlow(false)
    val isMutating: StateFlow<Boolean> = _isMutating
    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin


    init {
            refresh()

            viewModelScope.launch {
                _isAdmin.value = RequestRepository.isCurrentUserAdmin()
            }

            RequestRepository.startRequestsRealtime {
                refresh()
            }

        refresh()
        RequestRepository.startRequestsRealtime {
            refresh()
        }
    }

    /* -------------------- SAFE CALL HELPER -------------------- */

    private suspend fun <T> safeCall(
        block: suspend () -> T
    ): Result<T> {
        return try {
            Result.success(block())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /* -------------------- REFRESH -------------------- */

    fun refresh() {
        if (_isRefreshing.value) return

        viewModelScope.launch {
            _isRefreshing.value = true

            val result = safeCall {
                RequestRepository.fetchRequests()
            }

            result
                .onSuccess {
                    _requests.value = it
                    _error.value = null
                    _lastUpdated.value = System.currentTimeMillis()
                }
                .onFailure {
                    _error.value = "Unable to refresh. Check connection."
                }

            _isRefreshing.value = false
        }
    }

    /* -------------------- MUTATIONS (GUARDED) -------------------- */

    fun updateDate(id: String, date: String) {
        if (_isMutating.value) return

        viewModelScope.launch {
            _isMutating.value = true

            val result = safeCall {
                RequestRepository.updateRequestDate(id, date)
            }

            result
                .onSuccess { refresh() }
                .onFailure {
                    _error.value = "Failed to update date."
                }

            _isMutating.value = false
        }
    }

    fun updateDetails(id: String, title: String, description: String) {
        if (_isMutating.value) return

        viewModelScope.launch {
            _isMutating.value = true

            val result = safeCall {
                RequestRepository.updateRequestDetails(id, title, description)
            }

            result
                .onSuccess { refresh() }
                .onFailure {
                    _error.value = "Failed to update request details."
                }

            _isMutating.value = false
        }
    }

    fun cancel(id: String) {
        if (_isMutating.value) return

        viewModelScope.launch {
            _isMutating.value = true

            val result = safeCall {
                RequestRepository.cancelRequest(id)
            }

            result
                .onSuccess { refresh() }
                .onFailure {
                    _error.value = "Failed to cancel request."
                }

            _isMutating.value = false
        }
    }
    fun forceCancel(id: String) {
        viewModelScope.launch {
            RequestRepository.adminCancelRequest(id)
            refresh()
        }
    }

    fun forceComplete(id: String) {
        viewModelScope.launch {
            RequestRepository.adminCompleteRequest(id)
            refresh()
        }
    }


    override fun onCleared() {
        super.onCleared()
        RequestRepository.stopRequestsRealtime()
    }



}
