package com.example.handyhood.auth
import io.github.jan.supabase.gotrue.auth
import com.example.handyhood.data.remote.SupabaseClient
import io.github.jan.supabase.gotrue.providers.builtin.Email

object AuthRepository {

    private val auth = SupabaseClient.client.auth

    suspend fun signUp(email: String, password: String): AuthResult {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            AuthResult.Success(auth.currentUserOrNull()?.id ?: "")
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
            AuthResult.Success(auth.currentUserOrNull()?.id ?: "")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Login failed")
        }
    }

    suspend fun signOut() {
        auth.signOut()
    }

    fun currentUserId(): String? {
        return auth.currentUserOrNull()?.id
    }

    fun isLoggedIn(): Boolean {
        return auth.currentUserOrNull() != null
    }
}
