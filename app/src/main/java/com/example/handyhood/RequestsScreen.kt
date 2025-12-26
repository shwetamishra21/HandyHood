package com.example.handyhood.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.handyhood.data.RequestRepository
import com. example. handyhood. data. RequestsViewModel
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

    var isUpdating by remember { mutableStateOf(false) }
    var selectedRequestId by remember { mutableStateOf<String?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var showEditDialog by remember { mutableStateOf(false) }
    var editTitle by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }

    var showCancelDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Service Requests") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            when {
                error != null -> {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                requests.isEmpty() -> {
                    Text("No requests found.")
                }

                else -> {
                    requests
                        .filter { it["is_cancelled"] != true }
                        .forEach { req ->

                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            req["title"].toString(),
                                            fontWeight = FontWeight.Bold
                                        )

                                        if (req["status"] != "completed") {
                                            IconButton(onClick = {
                                                selectedRequestId = req["id"].toString()
                                                editTitle = req["title"].toString()
                                                editDescription = req["description"].toString()
                                                showEditDialog = true
                                            }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                                            }
                                        }
                                    }

                                    Spacer(Modifier.height(6.dp))
                                    Text("Category: ${req["category"]}")

                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = "Preferred Date: ${req["preferred_date"]}",
                                        modifier = Modifier.clickable(
                                            enabled = req["status"] != "completed" && !isUpdating
                                        ) {
                                            selectedRequestId = req["id"].toString()
                                            showDatePicker = true
                                        }
                                    )

                                    Spacer(Modifier.height(10.dp))
                                    Text(req["description"].toString())

                                    if (req["status"] != "completed") {
                                        Spacer(Modifier.height(12.dp))
                                        TextButton(
                                            onClick = {
                                                selectedRequestId = req["id"].toString()
                                                showCancelDialog = true
                                            },
                                            colors = ButtonDefaults.textButtonColors(
                                                contentColor = MaterialTheme.colorScheme.error
                                            )
                                        ) {
                                            Text("Cancel Request")
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
    if (showDatePicker && selectedRequestId != null) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
                selectedRequestId = null
            },
            confirmButton = {
                TextButton(onClick = {
                    val millis =
                        datePickerState.selectedDateMillis ?: System.currentTimeMillis()

                    val newDate = SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).format(Date(millis))

                    scope.launch {
                        isUpdating = true
                        RequestRepository.updateRequestDate(
                            selectedRequestId!!,
                            newDate
                        )
                        isUpdating = false
                    }

                    showDatePicker = false
                    selectedRequestId = null
                }) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    selectedRequestId = null
                }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    /* ---------- Edit Dialog ---------- */
    if (showEditDialog && selectedRequestId != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Request") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        RequestRepository.updateRequestDetails(
                            selectedRequestId!!,
                            editTitle,
                            editDescription
                        )
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

    /* ---------- Cancel Dialog ---------- */
    if (showCancelDialog && selectedRequestId != null) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Request?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            RequestRepository.cancelRequest(selectedRequestId!!)
                        }
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Yes, Cancel")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}
