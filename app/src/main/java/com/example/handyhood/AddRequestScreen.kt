package com.example.handyhood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.handyhood.data.RequestsRepository
import com.example.handyhood.ui.theme.LightBlueGradient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRequestScreen(
    navController: NavHostController,
    requestsRepository: RequestsRepository
) {
    var category by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var preferredDate by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }  // ✅ Date picker toggle

    val datePickerState = rememberDatePickerState()  // ✅ Material3 DatePicker
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    val isFormValid = category.isNotBlank()
            && title.isNotBlank()
            && description.isNotBlank()
            && preferredDate.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Service Request") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBlueGradient)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            ElevatedCard {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text("Service Details", fontWeight = FontWeight.Bold)

                    // Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category *") },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        val options = listOf(
                            "Electrician", "Plumber", "Carpenter",
                            "Painter", "Cleaning", "AC Repair"
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            options.forEach {
                                DropdownMenuItem(
                                    text = { Text(it) },
                                    onClick = {
                                        category = it
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description *") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )

                    // ✅ FIXED WORKING DATE PICKER FIELD
                    OutlinedTextField(
                        value = preferredDate,
                        onValueChange = { preferredDate = it },
                        label = { Text("Preferred Date *") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.CalendarToday, "Select Date")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        placeholder = { Text("Click calendar to select") }
                    )
                }
            }

            Button(
                enabled = isFormValid,
                onClick = {
                    scope.launch {
                        if (isFormValid) {
                            requestsRepository.addRequest(
                                category, title, description, preferredDate
                            )
                            focusManager.clearFocus()

                            // 🔥 Notify previous screen to refresh
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("refresh_requests", true)
                        }

                        navController.popBackStack()
                    }
                }
                ,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Request", fontWeight = FontWeight.Bold)
            }
        }

        // ✅ FIXED WORKING DATE PICKER DIALOG
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = {
                    showDatePicker = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                preferredDate = SimpleDateFormat(
                                    "dd/MM/yyyy",
                                    Locale.getDefault()
                                ).format(Date(millis))
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDatePicker = false }
                    ) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}
