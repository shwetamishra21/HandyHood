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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handyhood.auth.AuthResult
import com.example.handyhood.auth.SupabaseAuthViewModel
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

                    val authViewModel: SupabaseAuthViewModel = viewModel()
                    val authState by authViewModel.authState.collectAsState()

                    when (authState) {
                        is AuthResult.Success -> {
                            HandyHoodNavigation()
                        }
                        else -> {
                            LoginScreen(
                                onLoginSuccess = { }
                            )
                        }
                    }
                }
            }
        }
    }
}
