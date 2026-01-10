package com.example.handyhood.data.community

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class HireRequestRepositoryImpl(
    private val supabase: SupabaseClient
) : HireRequestRepository {

    private val isoFormatter = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        Locale.US
    ).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override suspend fun createHireRequest(
        providerId: UUID,
        message: String?
    ) {
        supabase.from("provider_hire_requests").insert(
            mapOf(
                "provider_id" to providerId.toString(),
                "message" to message
            )
        )
    }

    override suspend fun getIncomingRequests(): List<HireRequest> {
        val rows = supabase
            .from("provider_hire_requests")
            .select()
            .decodeList<Map<String, Any?>>()

        return rows.map { row ->
            HireRequest(
                id = UUID.fromString(row["id"] as String),
                requesterId = UUID.fromString(row["requester_id"] as String),
                providerId = UUID.fromString(row["provider_id"] as String),
                message = row["message"] as String?,
                status = row["status"] as String,
                createdAtMillis = isoFormatter
                    .parse(row["created_at"] as String)
                    ?.time ?: 0L
            )
        }
    }

    override suspend fun updateRequestStatus(
        requestId: UUID,
        status: String
    ) {
        supabase.from("provider_hire_requests")
            .update(
                mapOf("status" to status)
            ) {
                filter {
                    eq("id", requestId.toString())
                }
            }
    }
}
