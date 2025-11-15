package com.example.handyhood

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.handyhood.ui.theme.HandyHoodTheme
import com.example.handyhood.ui.theme.LightBlueGradient

data class Message(
    val senderName: String,
    val message: String,
    val time: String,
    val isRead: Boolean
)

@Composable
fun InboxScreen() {
    val messages = listOf(
        Message("John Smith", "Interested in your service!", "2h ago", false),
        Message("Sarah Wilson", "Great job yesterday!", "5h ago", true),
        Message("Mike Brown", "Dog walking tomorrow?", "1d ago", false)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlueGradient)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("Messages", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
            items(messages) { msg ->
                ElevatedCard(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (msg.isRead)
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(msg.senderName, fontWeight = FontWeight.SemiBold)
                            Text(msg.message, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(msg.time, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InboxPreview() {
    HandyHoodTheme { InboxScreen() }
}
