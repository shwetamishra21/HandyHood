package com.example.handyhood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handyhood.auth.AuthResult
import com.example.handyhood.auth.SupabaseAuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: SupabaseAuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var isSignup by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()  // ✅ Added for navigation

    val authState by viewModel.authState.collectAsState()

    /* ---------- AUTH SUCCESS ---------- */
    LaunchedEffect(authState) {
        if (authState is AuthResult.Success) {
            navController.navigate("dashboard") {  // ✅ Fixed: Use navController
                popUpTo("login") { inclusive = true }
                popUpTo("welcome") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isSignup) "Create Account" else "Login",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        /* ---------- EMAIL ---------- */
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        /* ---------- PASSWORD ---------- */
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        /* ---------- FORGOT PASSWORD ---------- */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { showForgotDialog = true }) {
                Text("Forgot password?")
            }
        }

        Spacer(Modifier.height(20.dp))

        /* ---------- LOGIN / SIGNUP ---------- */
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (isSignup) {
                    viewModel.signUp(email.trim(), password)
                } else {
                    viewModel.signIn(email.trim(), password)
                }
            }
        ) {
            Text(if (isSignup) "Sign Up" else "Login")
        }

        Spacer(Modifier.height(12.dp))

        /* ---------- TOGGLE ---------- */
        TextButton(onClick = { isSignup = !isSignup }) {
            Text(
                if (isSignup) "Already have an account? Login"
                else "New user? Create account"
            )
        }

        Spacer(Modifier.height(16.dp))

        /* ---------- STATE FEEDBACK ---------- */
        when (authState) {
            is AuthResult.Loading -> CircularProgressIndicator()
            is AuthResult.Error -> Text(
                text = (authState as AuthResult.Error).message,
                color = MaterialTheme.colorScheme.error
            )
            else -> {}
        }
    }

    /* ---------- FORGOT PASSWORD DIALOG ---------- */
    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = { Text("Reset password") },
            text = {
                Column {
                    Text(
                        "Enter your email and we'll send you a reset link.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = resetEmail.isNotBlank(),
                    onClick = {
                        viewModel.sendPasswordReset(resetEmail.trim())
                        showForgotDialog = false
                    }
                ) {
                    Text("Send")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
