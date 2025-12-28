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
    /* ---------- State from ViewModel ---------- */
    val requests by viewModel.requests.collectAsState()
    val error by viewModel.error.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val lastUpdated by viewModel.lastUpdated.collectAsState()
    val isMutating by viewModel.isMutating.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()

    /* ---------- UI State ---------- */
    var selectedId by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var showEditDialog by remember { mutableStateOf(false) }
    var editTitle by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    /* ---------- Scaffold ---------- */
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Requests") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            if (isRefreshing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (lastUpdated != null) {
                Text(
                    text = "Last updated: ${formatRelativeTime(lastUpdated!!)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
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
                            val requestId = req["id"].toString()

                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Column(Modifier.padding(16.dp)) {

                                    Text(
                                        text = req["title"].toString(),
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text("Category: ${req["category"]}")

                                    Text(
                                        text = "Preferred Date: ${req["preferred_date"]}",
                                        modifier = Modifier.clickable {
                                            selectedId = requestId
                                            showDatePicker = true
                                        }
                                    )

                                    Spacer(Modifier.height(8.dp))
                                    Text(req["description"].toString())

                                    Spacer(Modifier.height(12.dp))

                                    /* ---------- User Actions ---------- */
                                    Row {
                                        TextButton(
                                            enabled = !isMutating,
                                            onClick = {
                                                selectedId = requestId
                                                editTitle = req["title"].toString()
                                                editDescription = req["description"].toString()
                                                showEditDialog = true
                                            }
                                        ) {
                                            Text("Edit")
                                        }

                                        Spacer(Modifier.width(8.dp))

                                        TextButton(
                                            enabled = !isMutating,
                                            onClick = {
                                                viewModel.cancel(requestId)
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        "Request cancelled"
                                                    )
                                                }
                                            },
                                            colors = ButtonDefaults.textButtonColors(
                                                contentColor = MaterialTheme.colorScheme.error
                                            )
                                        ) {
                                            Text("Cancel")
                                        }
                                    }

                                    /* ---------- Admin Actions ---------- */
                                    if (isAdmin) {
                                        Spacer(Modifier.height(8.dp))

                                        Row {
                                            TextButton(
                                                onClick = {
                                                    viewModel.forceCancel(requestId)
                                                },
                                                colors = ButtonDefaults.textButtonColors(
                                                    contentColor = MaterialTheme.colorScheme.error
                                                )
                                            ) {
                                                Text("Force Cancel")
                                            }

                                            Spacer(Modifier.width(8.dp))

                                            TextButton(
                                                onClick = {
                                                    viewModel.forceComplete(requestId)
                                                }
                                            ) {
                                                Text("Force Complete")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /* ---------- Date Picker ---------- */
    if (showDatePicker && selectedId != null) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis ?: return@TextButton
                    val date = SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).format(Date(millis))

                    viewModel.updateDate(selectedId!!, date)

                    scope.launch {
                        snackbarHostState.showSnackbar("Preferred date updated")
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

    /* ---------- Edit Dialog ---------- */
    if (showEditDialog && selectedId != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit request") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Title") }
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text("Description") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateDetails(
                        selectedId!!,
                        editTitle,
                        editDescription
                    )

                    scope.launch {
                        snackbarHostState.showSnackbar("Request updated")
                    }

                    showEditDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/* ---------- Time helper ---------- */
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
