package com.example.handyhood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handyhood.data.community.SearchProvider
import com.example.handyhood.ui.community.SearchViewModel
import com.example.handyhood.ui.theme.LightBlueGradient
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier
) {
    // ✅ FIXED: ViewModel injected INSIDE composable body
    val viewModel: SearchViewModel = viewModel()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedProviderId by remember { mutableStateOf<UUID?>(null) }

    val filterOptions = listOf(
        "All",
        "Nearby",
        "Highest Rated",
        "Recent",
        "Price: Low to High"
    )

    val providers by viewModel.providers.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    // ✅ Load data on screen entry
    LaunchedEffect(Unit) {
        viewModel.loadOnce()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlueGradient)
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            /* ---------- HEADER + SEARCH ---------- */
            item {
                Text(
                    text = "Find Services",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search for services...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    }
                )
            }

            /* ---------- FILTERS ---------- */
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FilterList, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Filter by:", fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filterOptions) { option ->
                        FilterChip(
                            selected = selectedFilter == option,
                            onClick = { selectedFilter = option },
                            label = { Text(option) }
                        )
                    }
                }
            }

            /* ---------- CONTENT ---------- */
            when {
                loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                error != null -> {
                    item {
                        Text(
                            text = error ?: "Something went wrong",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                providers.isEmpty() -> {
                    item {
                        Text(
                            text = "No services available right now",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    items(
                        providers.filter {
                            searchQuery.isBlank() ||
                                    it.name.contains(searchQuery, ignoreCase = true) ||
                                    it.serviceType.contains(searchQuery, ignoreCase = true)
                        },
                        key = { it.id }
                    ) { provider ->
                        ProviderResultCard(
                            provider = provider,
                            onContactClick = {
                                selectedProviderId = provider.id
                            }
                        )
                    }
                }
            }
        }
    }

    /* ---------- HIRE REQUEST DIALOG ---------- */
    selectedProviderId?.let { providerId ->
        HireRequestDialog(
            providerId = providerId,
            onDismiss = { selectedProviderId = null }
        )
    }
}

@Composable
private fun ProviderResultCard(
    provider: SearchProvider,
    onContactClick: () -> Unit
) {
    ElevatedCard(
        onClick = onContactClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(provider.name, fontWeight = FontWeight.SemiBold)
                Text(
                    provider.serviceType,
                    color = MaterialTheme.colorScheme.primary
                )
                provider.experience?.let {
                    Spacer(Modifier.height(2.dp))
                    Text(it, style = MaterialTheme.typography.bodySmall)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
private fun HireRequestDialog(
    providerId: UUID,
    onDismiss: () -> Unit
) {
    var message by remember { mutableStateOf("") }
    var sending by remember { mutableStateOf(false) }
    var success by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Contact Provider") },
        text = {
            Column {
                if (success) {
                    Text("Request sent successfully!")
                } else {
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Message (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            if (!success) {
                TextButton(
                    enabled = !sending,
                    onClick = {
                        sending = true
                        // TODO: Implement backend call
                        success = true
                        sending = false
                    }
                ) {
                    Text(if (sending) "Sending..." else "Send")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (success) "Close" else "Cancel")
            }
        }
    )
}
