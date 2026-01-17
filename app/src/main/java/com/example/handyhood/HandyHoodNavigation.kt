package com.example.handyhood

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.handyhood.ui.screens.*
import com.example.handyhood.data.ActivityViewModel
import androidx. compose. ui. graphics. vector. ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Rounded.Home)
    object Search : Screen("search", "Search", Icons.Rounded.Search)
    object Inbox : Screen("inbox", "Inbox", Icons.Rounded.Inbox)
    object Profile : Screen("profile", "Profile", Icons.Rounded.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HandyHoodNavigation() {

    val navController = rememberNavController()

    // ⭐ REQUIRED for top bar recomposition
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route

    val activityViewModel: ActivityViewModel = viewModel()
    val hasUnread by activityViewModel.hasUnread.collectAsState()

    val bottomScreens = listOf(
        Screen.Dashboard,
        Screen.Search,
        Screen.Inbox,
        Screen.Profile
    )

    Scaffold(
        topBar = {
            if (route == Screen.Dashboard.route) {
                CenterAlignedTopAppBar(
                    title = { Text("HandyHood") },
                    actions = {
                        IconButton(onClick = { navController.navigate("activity") }) {
                            BadgedBox(
                                badge = { if (hasUnread) Badge() }
                            ) {
                                Icon(
                                    Icons.Rounded.NotificationsActive,
                                    contentDescription = "Activity",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (route == Screen.Dashboard.route) {
                FloatingActionButton(
                    onClick = { navController.navigate("add_request") }
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                }
            }
        },
        bottomBar = {
            NavigationBar {
                bottomScreens.forEach { screen ->
                    NavigationBarItem(
                        selected = route == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(padding)
        ) {

            composable(Screen.Dashboard.route) {
                DashboardScreen(navController, "User")
            }

            composable(Screen.Search.route) { SearchScreen() }

            composable(Screen.Inbox.route) {
                InboxScreen(emptyList(), {})
            }

            composable(Screen.Profile.route) {
                ProfileScreen("User", {})
            }

            composable("add_request") {
                AddRequestScreen(navController, com.example.handyhood.data.RequestsRepositoryImpl())
            }

            composable("requests") {
                RequestsScreen(navController)
            }

            composable("activity") {
                ActivityScreen(navController)
            }
        }
    }
}
