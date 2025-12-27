package com.example.handyhood.data

import com.example.handyhood.data.remote.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.broadcastFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject

object RequestRepository {

    private val auth = SupabaseClient.client.auth
    private val db = SupabaseClient.client.postgrest
    private val realtime = SupabaseClient.client.realtime

    /* -------------------- CRUD -------------------- */

    suspend fun addRequest(
        category: String,
        title: String,
        description: String,
        preferredDate: String
    ) {
        val user = auth.currentUserOrNull()
            ?: throw IllegalStateException("User not logged in")

        db.from("requests").insert(
            mapOf(
                "user_id" to user.id,
                "category" to category,
                "title" to title,
                "description" to description,
                "preferred_date" to preferredDate,
                "status" to "pending" // ✅ Day 8.1
            )
        )
    }


    suspend fun fetchRequests(): List<Map<String, Any?>> {
        val user = auth.currentUserOrNull()
            ?: throw IllegalStateException("User not logged in")

        val rows: List<JsonObject> = db
            .from("requests")
            .select {
                filter { eq("user_id", user.id) }
            }
            .decodeList()

        return rows.map { it.mapValues { v -> v.value } }
    }

    suspend fun updateRequestDate(
        requestId: String,
        newDate: String
    ) {
        val user = auth.currentUserOrNull()
            ?: throw IllegalStateException("User not logged in")

        db.from("requests").update(
            mapOf("preferred_date" to newDate)
        ) {
            filter {
                eq("id", requestId)
                eq("user_id", user.id)
            }
        }
    }

    suspend fun updateRequestDetails(
        requestId: String,
        newTitle: String,
        newDescription: String
    ) {
        val user = auth.currentUserOrNull()
            ?: throw IllegalStateException("User not logged in")

        db.from("requests").update(
            mapOf(
                "title" to newTitle,
                "description" to newDescription
            )
        ) {
            filter {
                eq("id", requestId)
                eq("user_id", user.id)
            }
        }
    }

    suspend fun cancelRequest(requestId: String) {
        val user = auth.currentUserOrNull()
            ?: throw IllegalStateException("User not logged in")

        db.from("requests").update(
            mapOf("is_cancelled" to true)
        ) {
            filter {
                eq("id", requestId)
                eq("user_id", user.id)
            }
        }
    }

    suspend fun acceptRequest(requestId: String) {
        val user = auth.currentUserOrNull()
            ?: throw IllegalStateException("Not authenticated")

        db.from("requests").update(
            mapOf(
                "status" to "accepted",
                "provider_id" to user.id
            )
        ) {
            filter { eq("id", requestId) }
        }
    }

    suspend fun completeRequest(requestId: String) {
        val user = auth.currentUserOrNull()
            ?: throw IllegalStateException("Not authenticated")

        db.from("requests").update(
            mapOf("status" to "completed")
        ) {
            filter { eq("id", requestId) }
        }
    }

    suspend fun fetchPendingRequestsForProvider(): List<Map<String, Any?>> {
        val user = auth.currentUserOrNull()
            ?: throw IllegalStateException("Not authenticated")

        val rows: List<JsonObject> = db
            .from("requests")
            .select {
                filter { eq("status", "pending") }
            }
            .decodeList()

        return rows.map { it.mapValues { v -> v.value } }
    }





    /* -------------------- REALTIME (Day 7.2) -------------------- */

    fun startRequestsRealtime(onChange: () -> Unit) {
        val user = auth.currentUserOrNull() ?: return

        val channel = realtime.channel("requests-global")

        CoroutineScope(Dispatchers.IO).launch {
            channel.subscribe()

            channel
                .broadcastFlow<JsonObject>("*")
                .collect {
                    onChange()
                }
        }
    }
}
