package com.example.handyhood.data.community

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.util.UUID

class SearchRepositoryImpl(
    private val supabase: SupabaseClient
) : SearchRepository {

    override suspend fun searchPeople(): List<SearchPerson> {
        val rows = supabase
            .from("search_people_admin")
            .select()
            .decodeList<Map<String, Any?>>()

        return rows.map { row ->
            SearchPerson(
                id = UUID.fromString(row["id"] as String),
                name = row["name"] as String,
                email = row["email"] as String
            )
        }
    }

    override suspend fun searchProviders(): List<SearchProvider> {
        val rows = supabase
            .from("search_providers")
            .select()
            .decodeList<Map<String, Any?>>()

        return rows.map { row ->
            SearchProvider(
                id = UUID.fromString(row["id"] as String),
                name = row["name"] as String,
                serviceType = row["service_type"] as String,
                experience = row["experience"] as String?
            )
        }
    }
}
