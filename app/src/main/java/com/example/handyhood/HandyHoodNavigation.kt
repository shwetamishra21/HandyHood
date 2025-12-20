package com.example.handyhood

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import io.github.jan.supabase.gotrue.auth
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.handyhood.auth.AuthResult
import com.example.handyhood.auth.SupabaseAuthViewModel
import com.example.handyhood.data.remote.SupabaseClient
import com.example.handyhood.ui.screens.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Welcome : Screen("welcome", "Welcome", Icons.Default.Home)
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Search : Screen("search", "Search", Icons.Default.Search)
    object Inbox : Screen("inbox", "Inbox", Icons.Default.Inbox)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Requests : Screen("requests", "My Requests", Icons.Default.List)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HandyHoodNavigation() {

    val navController = rememberNavController()
    val authViewModel: SupabaseAuthViewModel = viewModel()

    // ✅ FIX: observe authState
    val authState by authViewModel.authState.collectAsState()
    val isLoggedIn = authState is AuthResult.Success

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavScreens = listOf(
        Screen.Dashboard,
        Screen.Search,
        Screen.Inbox,
        Screen.Profile
    )

    val showBottomBar = currentRoute in bottomNavScreens.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    screens = bottomNavScreens,
                    currentRoute = currentRoute,
                    onScreenSelected = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn)
                Screen.Dashboard.route
            else
                Screen.Welcome.route,
            modifier = Modifier.padding(paddingValues)
        ) {

            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    onGetStartedClick = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen(navController)
            }

            composable(Screen.Search.route) {
                SearchScreen()
            }

            composable(Screen.Inbox.route) {
                InboxScreen()
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    userName = SupabaseClient.client.auth
                        .currentUserOrNull()
                        ?.email ?: "User",
                    onSignOut = {
                        authViewModel.signOut()
                        navController.navigate(Screen.Welcome.route) {
                            popUpTo(0)
                        }
                    }
                )
            }

            composable(Screen.Requests.route) {
                RequestsScreen(navController)
            }

            composable("add_request") {
                AddRequestScreen(navController)
            }
        }
    }
}
