package com.example.handyhood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.handyhood.data.ActivityViewModel
import com.example.handyhood.data.Post
import com.example.handyhood.data.PostsRepository
import com.example.handyhood.ui.theme.LightBlueGradient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    userEmail: String
) {
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val activityViewModel: ActivityViewModel = viewModel()
    val hasUnread by activityViewModel.hasUnread.collectAsState()

    LaunchedEffect(Unit) {
        try {
            posts = PostsRepository.fetchPosts()
        } catch (e: Exception) {
            errorMessage = "Failed to load posts"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("HandyHood", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(
                        onClick = { navController.navigate("activity") }
                    ) {
                        BadgedBox(
                            badge = {
                                if (hasUnread) Badge()
                            }
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Activity"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_request") }
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Request")
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBlueGradient)
                .padding(padding)
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {

                /* ---------- WELCOME ---------- */
                item {
                    ElevatedCard {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Welcome 👋",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                text = userEmail,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Stay connected with your neighborhood",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                /* ---------- QUICK ACTIONS ---------- */
                item {
                    ElevatedCard {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Quick Actions",
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(Modifier.height(12.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                                FilledTonalButton(
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        coroutineScope.launch {
                                            isLoading = true
                                            try {
                                                posts = PostsRepository.fetchPosts()
                                                snackbarHostState.showSnackbar("Feed refreshed")
                                            } catch (e: Exception) {
                                                snackbarHostState.showSnackbar("Refresh failed")
                                            } finally {
                                                isLoading = false
                                            }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Refresh, null)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Refresh")
                                }

                                FilledTonalButton(
                                    modifier = Modifier.weight(1f),
                                    onClick = { navController.navigate("add_request") }
                                ) {
                                    Icon(Icons.Default.Add, null)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Request")
                                }
                            }
                        }
                    }
                }

                /* ---------- MY REQUESTS ---------- */
                item {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("requests") }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "My Requests",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Track ongoing services",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(Icons.Default.List, null)
                        }
                    }
                }

                /* ---------- COMMUNITY FEED ---------- */
                item {
                    Text(
                        text = "Community",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                when {
                    isLoading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    errorMessage != null -> {
                        item {
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    posts.isEmpty() -> {
                        item {
                            Text(
                                text = "No community posts yet",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    else -> {
                        items(posts) { post ->
                            ElevatedCard {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = post.author,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = post.title,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = post.content,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
