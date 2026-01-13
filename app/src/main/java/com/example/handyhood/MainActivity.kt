package com.example.handyhood

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handyhood.auth.AuthResult
import com.example.handyhood.auth.SupabaseAuthViewModel
import com.example.handyhood.data.remote.SupabaseClient
import com.example.handyhood.ui.screens.LoginScreen
import com.example.handyhood.ui.theme.HandyHoodTheme
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var startRoute by mutableStateOf<Screen?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleDeepLink(intent)

        setContent {
            HandyHoodTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authViewModel: SupabaseAuthViewModel = viewModel()
                    val authState by authViewModel.authState.collectAsState()

                    when {
                        startRoute != null -> {
                            HandyHoodNavigation(startDestination = startRoute!!)
                        }

                        authState is AuthResult.Success -> {
                            HandyHoodNavigation()
                        }

                        else -> {
                            LoginScreen()
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val uri: Uri = intent?.data ?: return

        if (uri.scheme == "handyhood" && uri.host == "reset") {
            lifecycleScope.launch {
                try {
                    SupabaseClient.client.auth.exchangeCodeForSession(uri.toString())
                    startRoute = Screen.ResetPassword
                } catch (_: Exception) {
                    // user can retry reset
                }
            }
        }
    }
}
