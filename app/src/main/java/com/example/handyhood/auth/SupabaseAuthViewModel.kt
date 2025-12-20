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
            // ✅ CORRECT for supabase-kt 2.x
            auth.loadFromStorage()

            val user = auth.currentUserOrNull()
            _authState.value =
                if (user != null) AuthResult.Success(user)
                else AuthResult.Idle
        }
    }

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
}
