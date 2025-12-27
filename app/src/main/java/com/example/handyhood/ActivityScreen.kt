package com.example.handyhood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handyhood.data.ActivityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel = viewModel()
) {
    val activities by viewModel.activities.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.markAllRead()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Activity") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(activities) { act ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(act["type"].toString(), style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(act["message"].toString())
                    }
                }
            }
        }
    }
}
