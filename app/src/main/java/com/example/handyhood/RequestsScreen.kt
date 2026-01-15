package com.example.handyhood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handyhood.data.RequestsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen() {
    // ✅ FIXED: ViewModel injected INSIDE composable body
    val viewModel: RequestsViewModel = viewModel()

    val requests by viewModel.requests.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    // ✅ Auto-load on screen entry
    LaunchedEffect(Unit) {
        viewModel.loadRequests()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Requests") }
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
