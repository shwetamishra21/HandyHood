package com.example.handyhood.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.handyhood.data.UserInfo
import com.example.handyhood.data.backend.BackendAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Clean, Supabase-free Auth ViewModel.
 *
 * NOTE: This file no longer depends on Supabase. It delegates auth operations to `BackendAuth`.
 * Replace `BackendAuth`'s stub implementations with your chosen backend (Firestore/Auth/REST/etc).
 */

sealed class AuthResult {
    object Idle : AuthResult()
    object Loading : AuthResult()
    data class Success(val user: UserInfo?) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthViewModel : ViewModel() {

    // Delegates to a platform-agnostic backend interface (currently a stub).
    private val auth = BackendAuth

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authState: StateFlow<AuthResult> = _authState

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthResult.Loading
                val user = auth.signUp(email, password)
                _authState.value = AuthResult.Success(user)
            } catch (e: Exception) {
                _authState.value = AuthResult.Error(e.message ?: "Signup failed")
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthResult.Loading
                val user = auth.signIn(email, password)
                _authState.value = AuthResult.Success(user)
            } catch (e: Exception) {
                _authState.value = AuthResult.Error(e.message ?: "Login failed")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                auth.signOut()
                _authState.value = AuthResult.Idle
            } catch (e: Exception) {
                _authState.value = AuthResult.Error("Sign out failed: ${e.message}")
            }
        }
    }

    fun currentUserEmail(): String? {
        return auth.currentUserEmail()
    }
}
