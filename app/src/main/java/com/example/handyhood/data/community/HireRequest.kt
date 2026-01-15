package com.example.handyhood.data.community

import java.util.UUID
import kotlinx.serialization.Serializable


data class HireRequest(
    val id: UUID,
    val requesterId: UUID,
    val providerId: UUID,
    val message: String?,
    val status: String,
    val createdAtMillis: Long
)
