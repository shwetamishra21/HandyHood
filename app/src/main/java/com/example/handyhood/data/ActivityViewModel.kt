package com.example.handyhood.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActivityViewModel : ViewModel() {  // ✅ No-arg constructor = NO CRASH

    private val _activities =
        MutableStateFlow<List<Map<String, Any?>>>(emptyList())
    val activities: StateFlow<List<Map<String, Any?>>> = _activities

    private val _hasUnread = MutableStateFlow(false)
    val hasUnread: StateFlow<Boolean> = _hasUnread

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                // ✅ Direct calls - no injection needed
                val list = ActivityRepository.fetchMyActivities()
                _activities.value = list
                _hasUnread.value = list.any { it["is_read"] == false }
            } catch (_: Exception) {
                // Silent fail — activity feed is non-critical
            }
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            ActivityRepository.markAllRead()
            refresh()
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}
