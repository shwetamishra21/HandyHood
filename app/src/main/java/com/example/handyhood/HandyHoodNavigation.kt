package com.example.handyhood

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.handyhood.auth.SupabaseAuthViewModel
import com.example.handyhood.ui.screens.*
import java.util.UUID

sealed class Screen(val route: String, val title: String, val icon: ImageVector?) {
    object Welcome : Screen("welcome", "Welcome", null)
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    object Search : Screen("search", "Search", Icons.Default.Search)
    object Inbox : Screen("inbox", "Inbox", Icons.Default.Inbox)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object ResetPassword : Screen("reset_password", "Reset Password", null)

    object Chat : Screen("chat/{id}", "Chat", null) {
        fun createRoute(id: UUID) = "chat/$id"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HandyHoodNavigation(
    startDestination: Screen = Screen.Dashboard
) {
    val navController = rememberNavController()
    val authViewModel: SupabaseAuthViewModel = viewModel()

    val bottomNavScreens = listOf(
        Screen.Dashboard,
        Screen.Search,
        Screen.Inbox,
        Screen.Profile
    )

    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            if (bottomNavScreens.any { it.route == currentRoute }) {
                NavigationBar {
                    bottomNavScreens.forEach { screen ->
                        NavigationBarItem(
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    restoreState = true
                                    launchSingleTop = true
                                }
                            },
                            icon = {
                                Icon(screen.icon!!, screen.title)
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
            startDestination = startDestination.route,
            modifier = Modifier.padding(padding)
        ) {

            composable(Screen.Dashboard.route) {
                DashboardScreen(navController, "User")
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
                    userName = "User",
                    onSignOut = {
                        authViewModel.signOut()
                        navController.navigate(Screen.Welcome.route) {
                            popUpTo(0)
                        }
                    }
                )
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
            ) { entry ->
                val id = UUID.fromString(entry.arguments!!.getString("id")!!)
                ChatScreen(
                    title = "Conversation",
                    messages = emptyList(),
                    onSend = {},
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
