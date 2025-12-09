package com.example.handyhood.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * RequestRepository - placeholder implementation without Supabase.
 *
 * TODO: Replace these stubs with real backend implementations (e.g. Firestore collections,
 * REST endpoints, GraphQL etc.)
 */

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

        try {
            // TODO: Implement backend call to insert a request with `requestData`
            // e.g. Firestore: db.collection("requests").add(requestData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchRequests(): List<Map<String, Any?>> =
        withContext(Dispatchers.IO) {
            try {
                // TODO: Implement backend query to fetch requests
                emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
}
