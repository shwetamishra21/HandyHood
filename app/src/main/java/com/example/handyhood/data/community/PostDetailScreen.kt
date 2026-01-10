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
import com.example.handyhood.data.community.CommunityPost
import com.example.handyhood.data.community.PostComment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    post: CommunityPost,
    isAdmin: Boolean,
    commentsViewModel: PostCommentsViewModel = viewModel()
) {
    val comments by commentsViewModel.comments.collectAsState()
    val loading by commentsViewModel.loading.collectAsState()
    val error by commentsViewModel.error.collectAsState()

    var newComment by remember { mutableStateOf("") }

    LaunchedEffect(post.id) {
        commentsViewModel.loadComments(post.id)
        commentsViewModel.startLiveUpdates(post.id)
    }

    DisposableEffect(Unit) {
        onDispose {
            commentsViewModel.stopLiveUpdates()
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post") }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // ── POST CARD ───────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(post.title, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(post.body)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        formatMillis(post.createdAtMillis),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Comments",
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(Modifier.height(8.dp))

            // ── COMMENTS LIST ───────────────────────────
            when {
                loading -> {
                    CircularProgressIndicator()
                }

                error != null -> {
                    Text(
                        error ?: "Error loading comments",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                comments.isEmpty() -> {
                    Text(
                        "No comments yet",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(comments, key = { it.id }) { comment ->
                            CommentRow(
                                comment = comment,
                                canDelete = isAdmin,
                                onDelete = {
                                    commentsViewModel.deleteComment(
                                        postId = post.id,
                                        commentId = comment.id
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // ── ADD COMMENT ─────────────────────────────
            if (!post.isLocked) {
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = newComment,
                    onValueChange = { newComment = it },
                    placeholder = { Text("Write a comment…") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (newComment.isNotBlank()) {
                            commentsViewModel.addComment(
                                postId = post.id,
                                body = newComment.trim()
                            )
                            newComment = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Post")
                }
            } else {
                Spacer(Modifier.height(12.dp))
                Text(
                    "Comments are locked by admin",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun CommentRow(
    comment: PostComment,
    canDelete: Boolean,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(comment.body)
                Spacer(Modifier.height(4.dp))
                Text(
                    formatMillis(comment.createdAtMillis),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            if (canDelete) {
                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}

private fun formatMillis(millis: Long): String {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    return sdf.format(Date(millis))
}
