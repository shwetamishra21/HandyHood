package com.example.handyhood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.handyhood.data.ActivityViewModel
import com.example.handyhood.ui.theme.BluePrimary
import com.example.handyhood.ui.theme.BlueSecondaryLight
import com.example.handyhood.ui.theme.LightBlueGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    navController: NavHostController? = null,
    viewModel: ActivityViewModel = viewModel()
) {
    val activities by viewModel.activities.collectAsState()

    LaunchedEffect(Unit) {
        try {
            viewModel.markAllRead()   // ✅ SAFE
        } catch (_: Exception) {}
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Activity",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    Icon(
                        Icons.Rounded.NotificationsActive,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBlueGradient)
                .padding(padding)
        ) {

            if (activities.isEmpty()) {
                EmptyActivityState()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(activities) { act ->
                        ActivityCard(
                            title = act["type"]?.toString().orEmpty(),
                            message = act["message"]?.toString().orEmpty(),
                            unread = act["is_read"] == false
                        )
                    }
                }
            }
        }
    }
}

/* ---------- ACTIVITY CARD ---------- */

@Composable
private fun ActivityCard(
    title: String,
    message: String,
    unread: Boolean
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                // bubble icon
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = BlueSecondaryLight.copy(alpha = 0.35f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            if (unread) Icons.Rounded.NotificationsActive
                            else Icons.Rounded.NotificationsNone,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    title.replaceFirstChar { it.uppercase() },
                    fontWeight = FontWeight.SemiBold,
                    color = BluePrimary,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/* ---------- EMPTY STATE ---------- */

@Composable
private fun EmptyActivityState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Surface(
            modifier = Modifier.size(110.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = BlueSecondaryLight.copy(alpha = 0.35f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Rounded.NotificationsNone,
                    contentDescription = null,
                    tint = BluePrimary,
                    modifier = Modifier.size(52.dp)
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            "You're all caught up!",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = BluePrimary
        )

        Spacer(Modifier.height(6.dp))

        Text(
            "No new updates or alerts right now.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
