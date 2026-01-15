package com.example.handyhood.data.remote

import com.example.handyhood.BuildConfig
import io.github.jan.supabase.SupabaseClient as SBClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.realtime.Realtime

object SupabaseClient {
    val client: SBClient by lazy {
        checkNotNull(BuildConfig.SUPABASE_URL.isNotBlank()) {
            "SUPABASE_URL not configured in local.properties"
        }
        checkNotNull(BuildConfig.SUPABASE_ANON_KEY.isNotBlank()) {
            "SUPABASE_ANON_KEY not configured in local.properties"
        }

        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
            install(Realtime)
        }
    }
}
