package com.example.handyhood.data.community

import java.util.UUID

data class SearchProvider(
    val id: UUID,
    val name: String,
    val serviceType: String,
    val experience: String?
)
