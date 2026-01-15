package com.example.handyhood.ui.community

import androidx.lifecycle.ViewModel
import com. example. handyhood. data. remote. SupabaseClient
import androidx.lifecycle.viewModelScope
import com.example.handyhood.data.community.SearchRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com. example. handyhood. data. community. SearchProvider

class SearchViewModel : ViewModel() {
    private val repository = SearchRepositoryImpl(SupabaseClient.client)  // ✅ Keep internal
    private var hasLoaded = false

    private val _providers = MutableStateFlow<List<SearchProvider>>(emptyList())
    val providers: StateFlow<List<SearchProvider>> = _providers
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadOnce() {
        if (hasLoaded) return
        hasLoaded = true
        loadProviders()
    }

    private fun loadProviders() = viewModelScope.launch {  // ✅ Perf: inline launch
        _loading.value = true
        try {
            _providers.value = repository.searchProviders()
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to load services"
        } finally {
            _loading.value = false
        }
    }
}
