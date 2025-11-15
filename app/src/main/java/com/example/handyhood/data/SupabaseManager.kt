package com.example.handyhood.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseManager {

    private const val SUPABASE_URL = "https://yhvlxpqdkhjzkulcmjkd.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inhodmx4cHFka2hqemt1bGNtamtkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI4NDI1NzQsImV4cCI6MjA3ODQxODU3NH0.yEn5IT4LNvq7-8p-fQmotzOQBu72r_np2Y1-28VQjiQ"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)          // ✔ works in your SDK version
        install(Postgrest)     // ✔ works
        install(Storage)       // ✔ works
    }
}

