package com.example.handyhood.data

import com.example.handyhood.data.remote.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.JsonObject

object RequestRepository {

    private val auth = SupabaseClient.client.auth
    private val db = SupabaseClient.client.postgrest

    // ✅ Day 4.3 — create request
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
                "preferred_date" to preferredDate
            )
        )
    }

    // ✅ Day 4.4 — fetch user requests (THIS WAS MISSING)
    suspend fun fetchRequests(): List<Map<String, Any?>> {
        val user = auth.currentUserOrNull()
            ?: throw IllegalStateException("User not logged in")

        val rows: List<JsonObject> = db
            .from("requests")
            .select {
                filter {
                    eq("user_id", user.id)
                }
            }
            .decodeList()

        // Convert JsonObject → Map<String, Any?>
        return rows.map { json ->
            json.mapValues { it.value }
        }
    }
}
