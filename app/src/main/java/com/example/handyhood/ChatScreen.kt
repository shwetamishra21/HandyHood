package com.example.handyhood.ui.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.handyhood.data.community.*
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: UUID,
    supabase: io.github.jan.supabase.SupabaseClient,
    repository: MessageRepository
) {
    val viewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(repository, supabase)
    )

    var input by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()

    LaunchedEffect(conversationId) {
        viewModel.loadMessages(conversationId)
        viewModel.startRealtime(conversationId)
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopRealtime() }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Chat") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    MessageBubble(msg)
                }
            }

            Divider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message…") },
                    singleLine = true
                )

                Spacer(Modifier.width(8.dp))

                Button(
                    enabled = input.isNotBlank(),
                    onClick = {
                        viewModel.sendMessage(conversationId, input)
                        input = ""
                    }
                ) {
                    Text("Send")
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: Message) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Text(
            text = message.body,
            modifier = Modifier.padding(12.dp)
        )
    }
}
