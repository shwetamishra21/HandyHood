package com.example.handyhood

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.handyhood.ui.theme.HandyHoodTheme
import com.example.handyhood.ui.theme.LightBlueGradient

@Composable
fun WelcomeScreen(
    navController: NavHostController  // ✅ Pass as parameter
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlueGradient)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(
                modifier = Modifier.size(120.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(Icons.Default.Home, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
            Text(
                "HandyHood",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Your Neighborhood Service Network",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            // ✅ Navigate to Login
            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.ArrowForward, null)
                Spacer(Modifier.width(8.dp))
                Text("Get Started")
            }

            TextButton(
                onClick = { navController.navigate("signup") }
            ) {
                Text("Create Account")
            }
        }
    }
}
