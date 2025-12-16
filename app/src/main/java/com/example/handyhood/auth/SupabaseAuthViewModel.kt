package com.example.handyhood.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SupabaseAuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authState: StateFlow<AuthResult> = _authState

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            _authState.value = AuthRepository.signUp(email, password)
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            _authState.value = AuthRepository.signIn(email, password)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            AuthRepository.signOut()
            _authState.value = AuthResult.Idle
        }
    }

    fun isLoggedIn(): Boolean {
        return AuthRepository.isLoggedIn()
    }
}
