package com.example.handyhood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handyhood.auth.AuthResult
import com.example.handyhood.auth.SupabaseAuthViewModel

@Composable
fun LoginScreen(
    viewModel: SupabaseAuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isSignup by remember { mutableStateOf(false) } // 🔑 toggle

    val authState by viewModel.authState.collectAsState()

    // Navigate after success (login OR signup)
    LaunchedEffect(authState) {
        if (authState is AuthResult.Success) {
            onLoginSuccess()
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

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible)
                            Icons.Default.Visibility
                        else
                            Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            visualTransformation =
                if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (isSignup) {
                    viewModel.signUp(email.trim(), password)
                } else {
                    viewModel.signIn(email.trim(), password)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSignup) "Sign Up" else "Login")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔁 Toggle Login <-> Signup
        TextButton(
            onClick = { isSignup = !isSignup }
        ) {
            Text(
                if (isSignup)
                    "Already have an account? Login"
                else
                    "New user? Create account"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (authState) {
            is AuthResult.Loading -> {
                CircularProgressIndicator()
            }

            is AuthResult.Error -> {
                Text(
                    text = (authState as AuthResult.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            else -> {}
        }
    }
}
