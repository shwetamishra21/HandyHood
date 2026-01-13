package com.example.handyhood

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import io. github. jan. supabase. gotrue. auth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.handyhood.auth.AuthResult
import com.example.handyhood.auth.SupabaseAuthViewModel
import com.example.handyhood.data.remote.SupabaseClient
import com.example.handyhood.ui.screens.*
import java.util.UUID

/* ---------- ROUTES ---------- */

sealed class Screen(val route: String, val title: String, val icon: ImageVector?) {
    object Welcome : Screen("welcome", "Welcome", null)
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    object Search : Screen("search", "Search", Icons.Default.Search)
    object Inbox : Screen("inbox", "Inbox", Icons.Default.Inbox)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Activity : Screen("activity", "Activity", null)
    object Requests : Screen("requests", "Requests", null)
    object AddRequest : Screen("add_request", "Add Request", null)
    object ResetPassword : Screen("reset_password", "Reset Password", null)

    object Chat : Screen("chat/{id}", "Chat", null) {
        fun createRoute(id: UUID) = "chat/$id"
    }
}

/* ---------- NAV ROOT ---------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HandyHoodNavigation() {

    val navController = rememberNavController()
    val authViewModel: SupabaseAuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavScreens = listOf(
        Screen.Dashboard,
        Screen.Search,
        Screen.Inbox,
        Screen.Profile
    )

    val showBottomBar = bottomNavScreens.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavScreens.forEach { screen ->
                        NavigationBarItem(
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = screen.icon!!,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) }
                        )
                    }
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = when (authState) {
                is AuthResult.Success -> Screen.Dashboard.route
                else -> Screen.Welcome.route
            },
            modifier = Modifier.padding(padding)
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
                DashboardScreen(
                    navController = navController,
                    userEmail = SupabaseClient.client
                        .auth
                        .currentUserOrNull()
                        ?.email ?: "User"
                )
            }

            composable(Screen.Search.route) {
                SearchScreen()
            }

            composable(Screen.Inbox.route) {
                InboxScreen(
                    conversations = emptyList(),
                    onConversationClick = { id ->
                        navController.navigate(Screen.Chat.createRoute(id))
                    }
                )
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

            composable(Screen.Activity.route) {
                ActivityScreen()
            }

            composable(Screen.Requests.route) {
                RequestsScreen(navController)
            }

            composable(Screen.AddRequest.route) {
                AddRequestScreen(navController)
            }

            composable(Screen.ResetPassword.route) {
                ResetPasswordScreen(
                    onPasswordUpdated = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(0)
                        }
                    }
                )
            }

            composable(
                route = Screen.Chat.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = UUID.fromString(
                    backStackEntry.arguments!!.getString("id")!!
                )

                ChatScreen(
                    title = "Conversation",
                    messages = emptyList(), // realtime wiring later
                    onSend = { /* handled later */ },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
