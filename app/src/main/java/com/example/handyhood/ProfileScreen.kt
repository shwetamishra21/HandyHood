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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
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
import com.example.handyhood.auth.AuthRepository
import com.example.handyhood.data.ProfileData
import com.example.handyhood.data.ProfileRepository
import com.example.handyhood.ui.theme.LightBlueGradient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userName: String,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val localProfile by ProfileRepository.loadProfile(context).collectAsState(
        initial = ProfileData("", "", "", "", "", false)
    )

    var dbProfile by remember { mutableStateOf<Map<String, Any?>?>(null) }

    LaunchedEffect(Unit) {
        dbProfile = AuthRepository.fetchUserProfile()
    }

    var name by remember {
        mutableStateOf(dbProfile?.get("name")?.toString() ?: userName)
    }
    var email by remember {
        mutableStateOf(dbProfile?.get("email")?.toString() ?: localProfile.email)
    }
    var neighborhood by remember { mutableStateOf(localProfile.neighborhood) }
    var birthday by remember { mutableStateOf(localProfile.birthday) }
    var verified by remember { mutableStateOf(localProfile.verified) }
    var imageUri by remember { mutableStateOf(localProfile.imageUri) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUri = it.toString() }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.SemiBold) }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBlueGradient)
                .padding(padding)
                .padding(16.dp)
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                /* ---------- AVATAR ---------- */
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

                Spacer(Modifier.height(12.dp))

                Text(
                    text = name.ifBlank { "Unnamed User" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = if (verified) Color(0xFFFFA726)
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (verified) "Community Verified" else "Not Verified",
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(24.dp))

                /* ---------- DETAILS CARD ---------- */
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = neighborhood,
                            onValueChange = { neighborhood = it },
                            label = { Text("Neighborhood") },
                            modifier = Modifier.fillMaxWidth()
                        )

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

                Spacer(Modifier.height(20.dp))

                /* ---------- ACTIONS ---------- */
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
                        .height(54.dp)
                ) {
                    Icon(Icons.Default.Edit, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onSignOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Icon(Icons.Default.Logout, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Logout")
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis =
                            datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                        birthday = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(Date(millis))
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
