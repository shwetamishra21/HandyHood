package com.example.handyhood.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RequestsViewModel(
    private val repository: RequestsRepository = RequestsRepositoryImpl()
) : ViewModel() {

    private val _requests = MutableStateFlow<List<ServiceRequest>>(emptyList())
    val requests: StateFlow<List<ServiceRequest>> = _requests

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadRequests() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _requests.value = repository.getMyRequests()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}
