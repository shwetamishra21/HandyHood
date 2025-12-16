package com.example.handyhood.auth

sealed class AuthResult {
    object Idle : AuthResult()
    object Loading : AuthResult()
    data class Success(val userId: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
