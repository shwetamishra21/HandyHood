package com.example.handyhood.ui.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handyhood.data.community.HireRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/* ────────────────────────────────────────────── */
/* SCREEN */
/* ────────────────────────────────────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HireRequestsScreen(
    viewModel: HireRequestsViewModel = viewModel()
) {
    val requests by viewModel.requests.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRequests()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hire Requests") }
            )
        }
    ) { padding ->

        when {
            loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            requests.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hire requests yet")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(requests, key = { it.id }) { request ->
                        HireRequestCard(
                            request = request,
                            onAccept = { viewModel.acceptRequest(request.id) },
                            onReject = { viewModel.rejectRequest(request.id) }
                        )
                    }
                }
            }
        }
    }
}

/* ────────────────────────────────────────────── */
/* CARD */
/* ────────────────────────────────────────────── */

@Composable
private fun HireRequestCard(
    request: HireRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Request from: ${request.requesterId}",
                style = MaterialTheme.typography.titleSmall
            )

            request.message?.let {
                Spacer(Modifier.height(6.dp))
                Text(it)
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = "Status: ${request.status}",
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(Modifier.height(4.dp))
            Text(
                text = formatMillis(request.createdAtMillis),
                style = MaterialTheme.typography.labelSmall
            )

            if (request.status == "pending") {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onAccept) {
                        Text("Accept")
                    }
                    OutlinedButton(onClick = onReject) {
                        Text("Reject")
                    }
                }
            }
        }
    }
}


private fun formatMillis(millis: Long): String {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    return sdf.format(Date(millis))
}
