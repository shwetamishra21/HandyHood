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
import androidx.navigation.NavHostController
import com.example.handyhood.data.RequestRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(navController: NavHostController) {

    var requests by remember { mutableStateOf<List<Map<String, Any?>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // 🔒 Day 5.5 state
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedRequestId by remember { mutableStateOf<String?>(null) }
    var isUpdating by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(Unit) {
        try {
            requests = RequestRepository.fetchRequests()
        } catch (e: Exception) {
            error = e.message ?: "Failed to load requests"
        } finally {
            isLoading = false
        }
    }

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
                isLoading -> {
                    CircularProgressIndicator()
                }

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
                    requests.forEach { req ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {

                                Text(
                                    req["title"].toString(),
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(Modifier.height(6.dp))
                                Text("Category: ${req["category"]}")

                                Spacer(Modifier.height(6.dp))

                                // ✅ Day 5.5 — Hardened editable preferred date
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Preferred Date: ${req["preferred_date"]}",
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable(
                                                enabled = req["status"] != "completed" && !isUpdating
                                            ) {
                                                selectedRequestId = req["id"].toString()
                                                showDatePicker = true
                                            }
                                    )

                                    if (req["status"] != "completed") {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit date",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Spacer(Modifier.height(10.dp))
                                Text(req["description"].toString())
                            }
                        }
                    }
                }
            }
        }
    }

    // ✅ Day 5.5 — Date picker with double-submit protection
    if (showDatePicker && selectedRequestId != null) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
                selectedRequestId = null
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis =
                            datePickerState.selectedDateMillis ?: System.currentTimeMillis()

                        val newDate = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(Date(millis))

                        scope.launch {
                            isUpdating = true
                            RequestRepository.updateRequestDate(
                                requestId = selectedRequestId!!,
                                newDate = newDate
                            )
                            requests = RequestRepository.fetchRequests()
                            isUpdating = false
                        }

                        showDatePicker = false
                        selectedRequestId = null
                    }
                ) { Text("Update") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    selectedRequestId = null
                }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
