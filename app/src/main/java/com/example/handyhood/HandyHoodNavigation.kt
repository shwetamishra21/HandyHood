package com.example.handyhood

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.handyhood.ui.screens.*
import androidx. compose. ui. Modifier
import androidx. compose. foundation. layout. padding

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector?
) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    object Search : Screen("search", "Search", Icons.Default.Search)
    object Inbox : Screen("inbox", "Inbox", Icons.Default.Inbox)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HandyHoodNavigation() {

    val navController = rememberNavController()

    val bottomNavScreens = listOf(
        Screen.Dashboard,
        Screen.Search,
        Screen.Inbox,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavScreens.forEach { screen ->
                    NavigationBarItem(
                        selected = false,
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
                            Icon(screen.icon!!, contentDescription = screen.title)
                        },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(padding)   // ✅ FIX
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
                    onConversationClick = {}
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    userName = "User",
                    onSignOut = {}
                )
            }
        }}
    }

