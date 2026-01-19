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
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com. example. handyhood. auth. SupabaseAuthViewModel
import com.example.handyhood.data.ProfileRepository
import com.example.handyhood.ui.theme.LightBlueGradient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userName: String,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authViewModel: SupabaseAuthViewModel = viewModel()

    val localProfile by ProfileRepository
        .loadProfile(context)
        .collectAsState(
            initial = com.example.handyhood.data.ProfileData(
                "", "", "", "", "", false
            )
        )

    var name by remember { mutableStateOf(localProfile.name.ifBlank { userName }) }
    var email by remember { mutableStateOf(localProfile.email) }
    var neighborhood by remember { mutableStateOf(localProfile.neighborhood) }
    var birthday by remember { mutableStateOf(localProfile.birthday) }
    var imageUri by remember { mutableStateOf(localProfile.imageUri) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUri = it.toString() }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBlueGradient)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ---------- Avatar ----------
            Image(
                painter = rememberAsyncImagePainter(
                    if (imageUri.isNotBlank()) imageUri
                    else "https://i.imgur.com/4M7IWwP.png"
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { imagePicker.launch("image/*") }
            )

            Spacer(Modifier.height(16.dp))

            // ---------- Editable Fields ----------
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
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
                onValueChange = { birthday = it },
                label = { Text("Birthday") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // ---------- Save ----------
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
                            localProfile.verified
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Rounded.Save, null)
                Spacer(Modifier.width(8.dp))
                Text("Save Changes")
            }

            Spacer(Modifier.height(12.dp))

            // ---------- Logout ----------
            OutlinedButton(
                onClick = {
                    authViewModel.signOut()
                    onSignOut()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Rounded.Logout, null)
                Spacer(Modifier.width(8.dp))
                Text("Logout")
            }
        }
    }
}
