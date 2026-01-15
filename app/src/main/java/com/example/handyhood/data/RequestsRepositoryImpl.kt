package com.example.handyhood.data

import com.example.handyhood.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.UUID
import io. github. jan. supabase. gotrue. auth

class RequestsRepositoryImpl : RequestsRepository {

    private val supabase = SupabaseClient.client

    override suspend fun addRequest(category: String, title: String, description: String, preferredDate: String) {
        val user = supabase.auth.currentUserOrNull() ?: return
        supabase.from("requests").insert(
            mapOf(
                "user_id" to user.id,
                "category" to category,
                "title" to title,
                "description" to description,
                "preferred_date" to preferredDate,
                "status" to "pending"
            )
        )
    }

    override suspend fun getMyRequests(): List<ServiceRequest> {
        val user = supabase.auth.currentUserOrNull() ?: return emptyList()

        val rows = supabase
            .from("requests")
            .select {
                filter {
                    eq("user_id", user.id)
                }
            }
            .decodeList<JsonObject>()

        return rows.mapNotNull { row ->
            try {
                ServiceRequest(
                    id = UUID.fromString(row["id"]!!.jsonPrimitive.content),
                    title = row["title"]!!.jsonPrimitive.content,
                    category = row["category"]!!.jsonPrimitive.content,
                    status = row["status"]!!.jsonPrimitive.content
                )
            } catch (_: Exception) {
                null
            }
        }
    }

    override suspend fun cancelRequest(requestId: UUID) {
        val user = supabase.auth.currentUserOrNull() ?: return
        supabase.from("requests").update(
            mapOf("is_cancelled" to true)
        ) {
            filter {
                eq("id", requestId.toString())
                eq("user_id", user.id)
            }
        }
    }

    override suspend fun updateRequestStatus(requestId: UUID, status: String) {
        supabase.from("requests").update(
            mapOf("status" to status)
        ) {
            filter {
                eq("id", requestId.toString())
            }
        }
    }
}
