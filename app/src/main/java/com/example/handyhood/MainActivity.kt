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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handyhood.auth.SupabaseAuthViewModel
import com.example.handyhood.data.remote.SupabaseClient
import com.example.handyhood.ui.screens.LoginScreen
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

                    // 🔐 Auth gate (UNCHANGED)
                    val authViewModel: SupabaseAuthViewModel = viewModel()
                    val isLoggedIn = authViewModel.isLoggedIn()

                    // Optional debug check (UNCHANGED)
                    SupabaseConnectionCheck()

                    if (isLoggedIn) {
                        HandyHoodNavigation()
                    } else {
                        LoginScreen(
                            onLoginSuccess = {
                                // auth state change triggers recomposition
                            }
                        )
                    }
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
