package com.example.handyhood.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.handyhood.data.remote.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SupabaseAuthViewModel : ViewModel() {

    private val auth = SupabaseClient.client.auth

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Loading)
    val authState: StateFlow<AuthResult> = _authState

    init {
        viewModelScope.launch {
            auth.loadFromStorage()
            _authState.value =
                if (auth.currentUserOrNull() != null)
                    AuthResult.Success(auth.currentUserOrNull()!!)
                else
                    AuthResult.Idle
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            _authState.value = AuthRepository.signIn(email, password)
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            _authState.value = AuthRepository.signUp(email, password)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            AuthRepository.signOut()
            _authState.value = AuthResult.Idle
        }
    }
    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            try {
                auth.resetPasswordForEmail(email)

                _authState.value = AuthResult.Error(
                    "Password reset email sent"
                )
            } catch (e: Exception) {
                _authState.value = AuthResult.Error(
                    e.message ?: "Failed to send reset email"
                )
            }
        }
    }
    fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthResult.Loading

                auth.updateUser {
                    password = newPassword
                }

                val user = auth.currentUserOrNull()
                _authState.value =
                    if (user != null) AuthResult.Success(user)
                    else AuthResult.Error("Session expired")

            } catch (e: Exception) {
                _authState.value = AuthResult.Error(
                    e.message ?: "Password update failed"
                )
            }
        }
    }


}
