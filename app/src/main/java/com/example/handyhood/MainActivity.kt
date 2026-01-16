package com.example.handyhood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.handyhood.data.RequestsRepositoryImpl
import com.example.handyhood.ui.screens.AddRequestScreen
import com.example.handyhood.ui.screens.DashboardScreen
import com.example.handyhood.ui.screens.RequestsScreen
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
                    val navController = rememberNavController()
                    AppNavigation(navController = navController)
                }
            }
        }
    }
}

@Composable
private fun AppNavigation(navController: NavHostController) {
    // ✅ FIXED: Create concrete implementation
    val requestsRepository = remember { RequestsRepositoryImpl() }

    NavHost(
        navController = navController,
        startDestination = "dashboard/{userEmail}",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("dashboard/{userEmail}") { backStackEntry ->
            val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""
            DashboardScreen(navController = navController, userEmail = userEmail)
        }
        composable("add_request") {
            AddRequestScreen(
                navController = navController,
                requestsRepository = requestsRepository  // ✅ PASSED CORRECTLY
            )
        }
        composable("requests") {
            RequestsScreen(navController = navController)
        }
        composable("activity") {
            Text("Activity Screen")
        }
    }
}
