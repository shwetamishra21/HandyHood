package com.example.handyhood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handyhood.auth.SupabaseAuthViewModel
import com.example.handyhood.ui.theme.HandyHoodTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HandyHoodTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authViewModel: SupabaseAuthViewModel = viewModel()
                    val authState by authViewModel.authState.collectAsState()

                    // Handle Supabase deep links (reset password / magic link)
                    LaunchedEffect(Unit) {
                        val deepLink = intent?.data?.toString()
                        if (deepLink != null && deepLink.startsWith("handyhood://auth")) {
                            authViewModel.handleDeepLink(deepLink)
                        }
                    }

                    // Your existing navigation composable (no params)
                    HandyHoodNavigation()
                }
            }
        }
    }
}
