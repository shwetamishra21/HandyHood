package com.example.handyhood.data

import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.handyhood.data.remote.SupabaseClient

object RequestRepository {

    suspend fun addRequest(
        category: String,
        title: String,
        description: String,
        preferredDate: String
    ) = withContext(Dispatchers.IO) {

        val requestData = mapOf(
            "category" to category,
            "title" to title,
            "description" to description,
            "preferred_date" to preferredDate
        )

        SupabaseClient.client
            .from("requests")
            .insert(requestData)
    }

    suspend fun fetchRequests(): List<Map<String, Any?>> =
        withContext(Dispatchers.IO) {
            SupabaseClient.client
                .from("requests")
                .select()
                .decodeList<Map<String, Any?>>()
        }
}
