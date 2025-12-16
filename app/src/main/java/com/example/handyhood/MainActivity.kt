package com.example.handyhood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.handyhood.data.remote.SupabaseClient
import com.example.handyhood.ui.theme.HandyHoodTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HandyHoodTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Optional debug check (safe)
                    SupabaseConnectionCheck()

                    // Main navigation
                    HandyHoodNavigation()
                }
            }
        }
    }
}

@Composable
fun SupabaseConnectionCheck() {

    LaunchedEffect(Unit) {
        try {
            val client = SupabaseClient.client
            println("✅ Supabase Connected Successfully: ${client.supabaseUrl}")
        } catch (e: Exception) {
            println("❌ Supabase Connection Failed: ${e.message}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HandyHoodTheme {
        HandyHoodNavigation()
    }
}
