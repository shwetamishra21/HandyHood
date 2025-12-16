package com.example.handyhood.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import com.example.handyhood.data.remote.SupabaseClient
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthResult {
    object Idle : AuthResult()
    object Loading : AuthResult()
    data class Success(val user: UserInfo?) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class SupabaseAuthViewModel : ViewModel() {

    private val auth = SupabaseClient.client.auth

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authState: StateFlow<AuthResult> = _authState

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthResult.Loading
                auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                _authState.value = AuthResult.Success(auth.currentUserOrNull())
            } catch (e: Exception) {
                _authState.value = AuthResult.Error(e.message ?: "Signup failed")
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthResult.Loading
                auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                _authState.value = AuthResult.Success(auth.currentUserOrNull())
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
        return auth.currentUserOrNull()?.email

    }
}
