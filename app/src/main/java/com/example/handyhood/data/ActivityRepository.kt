package com.example.handyhood.data

import com.example.handyhood.data.remote.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.JsonObject

object ActivityRepository {

    private val auth = SupabaseClient.client.auth
    private val db = SupabaseClient.client.postgrest

    suspend fun markAllRead() {
        val user = auth.currentUserOrNull() ?: return  // ✅ SAFE FIX

        db.from("activities").update(
            mapOf("is_read" to true)
        ) {
            filter {
                or {
                    eq("user_id", user.id)
                    eq("provider_id", user.id)
                }
            }
        }
    }

    suspend fun fetchMyActivities(): List<Map<String, Any?>> {
        val user = auth.currentUserOrNull() ?: return emptyList()  // ✅ SAFE FIX

        val rows = db
            .from("activities")
            .select {
                filter {
                    or {
                        eq("user_id", user.id)
                        eq("provider_id", user.id)
                    }
                }
                order("created_at", Order.DESCENDING)
                limit(20)
            }
            .decodeList<JsonObject>()

        return rows.map { json -> json.mapValues { it.value } }
    }
}
