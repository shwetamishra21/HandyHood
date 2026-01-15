package com.example.handyhood.data

import java.util.UUID

data class ServiceRequest(
    val id: UUID,
    val title: String,
    val category: String,
    val status: String
)
