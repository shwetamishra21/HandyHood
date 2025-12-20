package com.example.handyhood.auth

import com.example.handyhood.data.remote.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserInfo

object AuthRepository {

    private val auth = SupabaseClient.client.auth

    suspend fun signUp(email: String, password: String): AuthResult {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            AuthResult.Success(auth.currentUserOrNull())
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Signup failed")
        }
    }

    suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            AuthResult.Success(auth.currentUserOrNull())
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Login failed")
        }
    }

    suspend fun signOut() {
        auth.signOut()
    }

    fun currentUser(): UserInfo? {
        return auth.currentUserOrNull()
    }

    // ✅ SESSION-BASED (persists across app restarts)
    fun isLoggedIn(): Boolean {
        return auth.currentSessionOrNull() != null
    }
}
