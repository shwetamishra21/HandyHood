package com.example.handyhood.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.handyhood.data.ProfileData
import com.example.handyhood.data.ProfileRepository
import com.example.handyhood.ui.theme.LightBlueGradient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userName: String,                 // ✅ REQUIRED (from auth)
    onSignOut: () -> Unit              // ✅ REQUIRED
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Load profile data
    val profile by ProfileRepository.loadProfile(context).collectAsState(
        initial = ProfileData("", "", "", "", "", false)
    )

    var name by remember { mutableStateOf(profile.name.ifEmpty { userName }) } // ✅ use auth name
    var email by remember { mutableStateOf(profile.email) }
    var neighborhood by remember { mutableStateOf(profile.neighborhood) }
    var birthday by remember { mutableStateOf(profile.birthday) }
    var verified by remember { mutableStateOf(profile.verified) }
    var imageUri by remember { mutableStateOf(profile.imageUri) }

    // Image Picker
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUri = it.toString() }
    }

    // Birthday Picker
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlueGradient)
            .padding(16.dp)
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = rememberAsyncImagePainter(
                    if (imageUri.isNotEmpty()) imageUri
                    else "https://i.imgur.com/4M7IWwP.png"
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { imagePicker.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = verified,
                    onCheckedChange = { verified = it }
                )
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFA726)
                )
                Text("Community Verified", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(25.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = neighborhood,
                        onValueChange = { neighborhood = it },
                        label = { Text("Neighborhood") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = birthday,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Birthday") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    scope.launch {
                        ProfileRepository.saveProfile(
                            context,
                            name,
                            email,
                            neighborhood,
                            birthday,
                            imageUri,
                            verified
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text("Save Profile", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ✅ LOGOUT (final, correct)
            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text("Logout")
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                        birthday = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .format(Date(millis))
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
