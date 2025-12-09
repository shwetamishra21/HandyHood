package com.example.handyhood.data.backend

import com.example.handyhood.data.UserInfo

/**
 * Backend stubs keep the project Supabase-free and compilable.
 *
 * Replace the stub implementations below with real backend integrations (Firestore/REST/GraphQL/etc).
 *
 * Guidelines:
 *  - Implement `BackendAuth` signUp/signIn/signOut/getCurrentUser according to your chosen backend.
 *  - Implement separate backends for posts/requests/storage or create a unified client.
 */

/**
 * BackendAuth: simple interface-like singleton with suspendable operations.
 * Current implementation is a stub — returns null/defaults so app compiles.
 */
object BackendAuth {

    /**
     * Sign up a user. Replace with real sign-up call.
     * Return a UserInfo or throw an exception on error.
     */
    suspend fun signUp(email: String, password: String): UserInfo? {
        // TODO: Implement sign-up via chosen backend.
        // For now return null to indicate no user created.
        return null
    }

    suspend fun signIn(email: String, password: String): UserInfo? {
        // TODO: Implement sign-in via chosen backend.
        return null
    }

    suspend fun signOut() {
        // TODO: Implement sign-out (if needed).
    }

    fun currentUserEmail(): String? {
        // TODO: Return current user's email or null if not signed in.
        return null
    }
}
