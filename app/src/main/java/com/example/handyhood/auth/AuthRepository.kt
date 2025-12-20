package com.example.handyhood.auth

import com.example.handyhood.data.remote.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserInfo
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.JsonObject

object AuthRepository {

    private val auth = SupabaseClient.client.auth
    private val db = SupabaseClient.client.postgrest

    suspend fun signUp(email: String, password: String): AuthResult {
        return try {
            // 1️⃣ Create auth user
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            // 2️⃣ Persist user profile (Day 3)
            createUserProfileIfMissing()

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

    fun isLoggedIn(): Boolean {
        return auth.currentSessionOrNull() != null
    }

    // ✅ FULLY COMPATIBLE WITH YOUR SUPABASE-KT VERSION
    private suspend fun createUserProfileIfMissing() {
        val user = auth.currentUserOrNull() ?: return

        val rows: List<JsonObject> = db
            .from("users")
            .select {
                filter {
                    eq("id", user.id)
                }
            }
            .decodeList()

        if (rows.isEmpty()) {
            db.from("users").insert(
                mapOf(
                    "id" to user.id,
                    "email" to user.email
                )
            )
        }
    }
}
