package com.example.handyhood.data.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.serialization.json.buildJsonObject
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.RealtimeChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import io. github. jan. supabase. realtime. channel
import io. github. jan. supabase. realtime. realtime
import java.util.UUID

class ChatViewModel(
    private val repository: MessageRepository,
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private var channel: RealtimeChannel? = null

    fun loadMessages(conversationId: UUID) {
        viewModelScope.launch {
            _messages.value = repository.getMessages(conversationId)
        }
    }

    fun startRealtime(conversationId: UUID) {
        stopRealtime()

        channel = supabase.realtime.channel("chat:$conversationId")

        viewModelScope.launch {
            channel?.subscribe()
        }
    }

    fun sendMessage(conversationId: UUID, body: String) {
        if (body.isBlank()) return

        viewModelScope.launch {
            repository.sendMessage(conversationId, body)

            channel?.broadcast(
                event = "new_message",
                message = buildJsonObject { }
            )

            loadMessages(conversationId)
        }
    }

    fun stopRealtime() {
        channel?.let { ch ->
            viewModelScope.launch {
                supabase.realtime.removeChannel(ch)
            }
        }
        channel = null
    }

    override fun onCleared() {
        stopRealtime()
        super.onCleared()
    }
}
