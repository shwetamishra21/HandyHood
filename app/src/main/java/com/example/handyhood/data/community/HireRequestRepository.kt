package com.example.handyhood.data.community

import java.util.UUID

interface HireRequestRepository {

    suspend fun createHireRequest(
        providerId: UUID,
        message: String?
    )

    suspend fun getIncomingRequests(): List<HireRequest>

    suspend fun updateRequestStatus(
        requestId: UUID,
        status: String
    )
}
