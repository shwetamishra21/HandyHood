package com.example.handyhood.auth

import io.github.jan.supabase.gotrue.user.UserInfo

sealed class AuthResult {
    object Idle : AuthResult()
    object Loading : AuthResult()
    data class Success(val user: UserInfo?) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
