package com.example.handyhood.ui.screens

import androidx.compose.foundation.background
import com. example. handyhood. data. ActivityViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.handyhood.data.Post
import com.example.handyhood.data.PostsRepository
import com.example.handyhood.ui.theme.HandyHoodTheme
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


    // Initial fetch
    LaunchedEffect(Unit) {
        try {
            posts = PostsRepository.fetchPosts()
        } catch (e: Exception) {
            errorMessage = "Failed to load posts. Please try again."
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },

        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_request") }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Request")
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {

                // HEADER
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "HandyHood",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Connect with your neighborhood",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // WELCOME
                item {
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Welcome, $userEmail 👋",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(6.dp))
                            Text("Connect with neighbors & build your community.")
                        }
                    }
                }

                // QUICK ACTIONS
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
                                    Text("Add Request")
                                }
                            }
                        }
                    }
                }

                // MY REQUESTS
                item {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("requests") }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("My Requests", fontWeight = FontWeight.Bold)
                                Text("Track your service requests")
                            }
                            Icon(Icons.Default.List, null)
                        }
                    }
                }

                // POSTS
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
                            Text("No community posts yet.")
                        }
                    }

                    else -> {
                        items(posts) { post ->
                            ElevatedCard {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(post.author, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(4.dp))
                                    Text(post.title)
                                    Spacer(Modifier.height(8.dp))
                                    Text(post.content)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    HandyHoodTheme {
        DashboardScreen(
            navController = rememberNavController(),
            userEmail = "preview@handyhood.com"
        )
    }
}
