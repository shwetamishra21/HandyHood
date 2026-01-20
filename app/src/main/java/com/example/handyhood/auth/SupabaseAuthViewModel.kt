package com.example.handyhood.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.handyhood.data.remote.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthResult {
    object Idle : AuthResult()
    object Loading : AuthResult()
    data class Success(val user: Any?) : AuthResult()
    data class Error(val message: String) : AuthResult()
    data class Message(val message: String) : AuthResult()
    object RequirePasswordReset : AuthResult()
}

class SupabaseAuthViewModel : ViewModel() {

    private val auth = SupabaseClient.client.auth

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authState: StateFlow<AuthResult> = _authState

    init {
        viewModelScope.launch {
            // restore session if stored
            auth.loadFromStorage()
            val user = auth.currentUserOrNull()
            if (user != null) {
                _authState.value = AuthResult.Success(user)
            } else {
                _authState.value = AuthResult.Idle
            }
        }
    }

    /* ---------------- LOGIN ---------------- */

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

    /* ---------------- SIGN UP ---------------- */

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

    /* ---------------- LOGOUT ---------------- */

    fun signOut() {
        viewModelScope.launch {
            try {
                auth.signOut()
            } finally {
                _authState.value = AuthResult.Idle
            }
        }
    }

    /* ---------------- FORGOT PASSWORD (EMAIL) ---------------- */

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthResult.Loading

                // Version without redirectTo named parameter
                auth.resetPasswordForEmail(email)

                _authState.value = AuthResult.Message("Password reset email sent")
            } catch (e: Exception) {
                _authState.value =
                    AuthResult.Error(e.message ?: "Failed to send reset email")
            }
        }
    }


    /* ---------------- HANDLE DEEP LINK ---------------- */

    fun handleDeepLink(uri: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthResult.Loading

                // NOTE: if your supabase-kt version uses a different name,
                // change just this line to the correct function.
                auth.exchangeCodeForSession(uri)

                _authState.value = AuthResult.RequirePasswordReset
            } catch (e: Exception) {
                _authState.value =
                    AuthResult.Error(e.message ?: "Failed to handle link")
            }
        }
    }

    /* ---------------- RESET PASSWORD (AFTER LINK) ---------------- */

    fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthResult.Loading
                auth.updateUser {
                    password = newPassword
                }
                _authState.value = AuthResult.Success(auth.currentUserOrNull())
            } catch (e: Exception) {
                _authState.value =
                    AuthResult.Error(e.message ?: "Password update failed")
            }
        }
    }
}
