package com.example.handyhood.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActivityViewModel : ViewModel() {

    private val _activities =
        MutableStateFlow<List<Map<String, Any?>>>(emptyList())
    val activities: StateFlow<List<Map<String, Any?>>> = _activities

    private val _hasUnread = MutableStateFlow(false)
    val hasUnread: StateFlow<Boolean> = _hasUnread

    init {
        refresh()
        RequestRepository.startRequestsRealtime {
            refresh()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                val list = ActivityRepository.fetchMyActivities()
                _activities.value = list
                _hasUnread.value = list.any { it["is_read"] == false }
            } catch (_: Exception) {
            }
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            ActivityRepository.markAllRead()
            refresh()
        }
    }
}
