package com.example.handyhood.data.community

import com.example.handyhood.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class HireRequestRepositoryImpl : HireRequestRepository {

    private val isoFormatter = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        Locale.US
    ).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override suspend fun createHireRequest(providerId: UUID, message: String?) {
        SupabaseClient.client.from("provider_hire_requests").insert(
            mapOf(
                "provider_id" to providerId.toString(),
                "message" to message
            )
        )
    }

    override suspend fun getIncomingRequests(): List<HireRequest> {
        val rows = SupabaseClient.client
            .from("provider_hire_requests")
            .select()
            .decodeList<JsonObject>()

        return rows.mapNotNull { row ->
            try {
                HireRequest(
                    id = UUID.fromString(row["id"]?.jsonPrimitive?.content ?: return@mapNotNull null),
                    requesterId = UUID.fromString(row["requester_id"]?.jsonPrimitive?.content ?: return@mapNotNull null),
                    providerId = UUID.fromString(row["provider_id"]?.jsonPrimitive?.content ?: return@mapNotNull null),
                    message = row["message"]?.jsonPrimitive?.content,
                    status = row["status"]?.jsonPrimitive?.content ?: "pending",
                    createdAtMillis = isoFormatter
                        .parse(row["created_at"]?.jsonPrimitive?.content ?: "")
                        ?.time ?: 0L
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun updateRequestStatus(requestId: UUID, status: String) {
        SupabaseClient.client.from("provider_hire_requests")
            .update(mapOf("status" to status)) {
                filter {
                    eq("id", requestId.toString())
                }
            }
    }
}
