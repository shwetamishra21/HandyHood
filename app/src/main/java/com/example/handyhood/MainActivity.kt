package com.example.handyhood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.handyhood.ui.theme.HandyHoodTheme
import com.example.handyhood.data.SupabaseManager
import kotlinx.coroutines.launch

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
                    // ✅ Check Supabase connection when app starts
                    SupabaseConnectionCheck()

                    // ✅ Launch main navigation
                    HandyHoodNavigation()
                }
            }
        }
    }
}

@Composable
fun SupabaseConnectionCheck() {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val client = SupabaseManager.client
                println("✅ Supabase Connected Successfully: ${client.supabaseUrl}")
            } catch (e: Exception) {
                println("❌ Supabase Connection Failed: ${e.message}")
            }
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
