package com.example.handyhood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.handyhood.data.RequestsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(
    navController: NavHostController  // ✅ ADDED
) {
    val viewModel: RequestsViewModel = viewModel()
    val requests by viewModel.requests.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.loadRequests()
    }

    // 🔄 Listen for refresh trigger from AddRequestScreen
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    LaunchedEffect(savedStateHandle) {
        savedStateHandle
            ?.getStateFlow("refresh_requests", false)
            ?.collect { refresh ->
                if (refresh) {
                    viewModel.loadRequests()     // reload list
                    savedStateHandle["refresh_requests"] = false
                }
            }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Requests") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {  // ✅ ADDED
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                loading -> CircularProgressIndicator()

                error != null -> Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error
                )

                requests.isEmpty() -> Text(
                    text = "No requests yet",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(requests, key = { it.id }) { request ->
                            Card {
                                Column(Modifier.padding(16.dp)) {
                                    Text(
                                        text = request.title,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(text = request.category)
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = request.status,
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.labelLarge
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
