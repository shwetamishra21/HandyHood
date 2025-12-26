package com.example.handyhood.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.handyhood.data.RequestsViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(
    navController: NavHostController,
    viewModel: RequestsViewModel = viewModel()
) {
    val requests by viewModel.requests.collectAsState()
    val error by viewModel.error.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val lastUpdated by viewModel.lastUpdated.collectAsState()

    var selectedId by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // ✅ Snackbar state (7.3.4)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Requests") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) } // ✅ attach snackbar
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            // Realtime refresh indicator (7.3.1)
            if (isRefreshing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Last updated timestamp (7.3.3)
            if (lastUpdated != null) {
                Text(
                    text = "Last updated: ${formatRelativeTime(lastUpdated!!)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp, top = 6.dp, bottom = 6.dp)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                when {
                    error != null -> {
                        Text(error!!, color = MaterialTheme.colorScheme.error)
                    }

                    requests.isEmpty() -> {
                        Text("No requests found")
                    }

                    else -> {
                        requests.forEach { req ->
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(
                                        req["title"].toString(),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text("Category: ${req["category"]}")

                                    Text(
                                        "Preferred Date: ${req["preferred_date"]}",
                                        modifier = Modifier.clickable {
                                            selectedId = req["id"].toString()
                                            showDatePicker = true
                                        }
                                    )

                                    Spacer(Modifier.height(8.dp))
                                    Text(req["description"].toString())

                                    Spacer(Modifier.height(8.dp))

                                    // ✅ Cancel action with snackbar
                                    TextButton(
                                        onClick = {
                                            val id = req["id"].toString()
                                            viewModel.cancel(id)
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = "Request cancelled",
                                                    withDismissAction = true
                                                )
                                            }
                                        },
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Text("Cancel request")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Date picker → update date + snackbar
    if (showDatePicker && selectedId != null) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis!!
                    val date = SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).format(Date(millis))

                    viewModel.updateDate(selectedId!!, date)

                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Preferred date updated",
                            withDismissAction = true
                        )
                    }

                    showDatePicker = false
                }) {
                    Text("Update")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

/* ---------- Helper for relative time (7.3.3) ---------- */
private fun formatRelativeTime(ts: Long): String {
    val diff = System.currentTimeMillis() - ts
    val sec = diff / 1000
    val min = sec / 60
    val hr = min / 60

    return when {
        sec < 10 -> "just now"
        sec < 60 -> "$sec sec ago"
        min < 60 -> "$min min ago"
        hr < 24 -> "$hr hr ago"
        else -> "a while ago"
    }
}
