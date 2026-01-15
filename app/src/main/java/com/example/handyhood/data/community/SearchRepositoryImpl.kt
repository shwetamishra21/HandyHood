package com.example.handyhood.data.community

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.UUID
import kotlinx. serialization. json. contentOrNull

class SearchRepositoryImpl(
    private val supabase: SupabaseClient
) : SearchRepository {

    override suspend fun searchPeople(): List<SearchPerson> {
        val rows = supabase
            .from("search_people_admin")
            .select()
            .decodeList<JsonObject>()

        return rows.mapNotNull { row ->
            val id = row["id"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            val name = row["name"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            val email = row["email"]?.jsonPrimitive?.contentOrNull ?: ""

            SearchPerson(
                id = UUID.fromString(id),
                name = name,
                email = email
            )
        }
    }

    override suspend fun searchProviders(): List<SearchProvider> {
        val rows = supabase
            .from("search_providers")
            .select()
            .decodeList<JsonObject>()

        return rows.mapNotNull { row ->
            val id = row["id"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            val name = row["name"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            val serviceType =
                row["service_type"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            val experience = row["experience"]?.jsonPrimitive?.contentOrNull

            SearchProvider(
                id = UUID.fromString(id),
                name = name,
                serviceType = serviceType,
                experience = experience
            )
        }
    }
}
