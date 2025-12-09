package com.example.handyhood.data

/**
 * Light-weight user info model replacing Supabase-specific UserInfo.
 * Extend this with additional fields as needed.
 */

data class UserInfo(
    val uid: String? = null,
    val email: String? = null
)
