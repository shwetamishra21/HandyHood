package com.example.handyhood

import android.os.Build
import android.os.StrictMode
import android. os. Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.handyhood.auth.AuthResult
import com.example.handyhood.auth.SupabaseAuthViewModel
import com.example.handyhood.ui.theme.HandyHoodTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ STRICTMODE - Catch main thread violations
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build()
            )
        }

        enableEdgeToEdge()
        setContent {
            HandyHoodTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val authViewModel: SupabaseAuthViewModel = viewModel()
                    val authState by authViewModel.authState.collectAsState()

                    when (authState) {
                        is AuthResult.Success -> HandyHoodNavigation()
                        else -> WelcomeScreen(navController)
                    }
                }
            }
        }
    }
}
