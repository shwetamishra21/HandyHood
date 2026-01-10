package com.example.handyhood.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.handyhood.data.community.SearchPerson
import com.example.handyhood.data.community.SearchProvider
import com.example.handyhood.data.community.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: SearchRepository
) : ViewModel() {

    private val _people = MutableStateFlow<List<SearchPerson>>(emptyList())
    val people: StateFlow<List<SearchPerson>> = _people

    private val _providers = MutableStateFlow<List<SearchProvider>>(emptyList())
    val providers: StateFlow<List<SearchProvider>> = _providers

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadPeople() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _people.value = repository.searchPeople()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadProviders() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _providers.value = repository.searchProviders()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}
