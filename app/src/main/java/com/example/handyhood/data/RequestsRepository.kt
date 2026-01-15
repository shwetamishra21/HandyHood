package com.example.handyhood.data

import java.util.UUID

interface RequestsRepository {
    suspend fun addRequest(
        category: String,
        title: String,
        description: String,
        preferredDate: String
    )

    suspend fun getMyRequests(): List<ServiceRequest>

    suspend fun cancelRequest(requestId: UUID)

    suspend fun updateRequestStatus(requestId: UUID, status: String)
}
