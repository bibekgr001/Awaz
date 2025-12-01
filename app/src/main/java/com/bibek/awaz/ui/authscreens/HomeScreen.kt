package com.bibek.awaz.ui.authscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bibek.awaz.viewmodel.AuthViewModel
import com.bibek.awaz.viewmodel.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AuthViewModel,
    onOpenProfile: () -> Unit
) {
    var caption by remember { mutableStateOf("") }
    var posts by remember { mutableStateOf(listOf<Post>()) }

    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    // Banner visibility
    var showUserBanner by remember { mutableStateOf(true) }

    // Fetch signed-in email
    val signedInEmail = viewModel.currentUser?.email ?: "Unknown"

    // Listen to posts
    LaunchedEffect(Unit) {
        viewModel.listenToPosts(
            onUpdate = { posts = it },
            onError = { snackbarMessage = it }
        )
    }

    // Snackbar
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Awaz") },
                actions = {
                    TextButton(onClick = onOpenProfile) {
                        Text("Profile")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            // === SIGNED IN BANNER ===
            if (showUserBanner) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Signed in as: $signedInEmail",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        TextButton(onClick = { showUserBanner = false }) {
                            Text("✕")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                placeholder = { Text("What's on your mind?") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (caption.isNotBlank()) {
                        viewModel.createPost(
                            caption,
                            null,
                            onSuccess = {
                                caption = ""
                                snackbarMessage = "Posted!"
                            },
                            onFailure = {
                                snackbarMessage = it
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Post")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(posts) { post ->
                    PostItem(
                        post = post,
                        onClick = {},
                        onLike = { viewModel.toggleLike(post.id) },
                        onDelete = { id -> viewModel.deletePost(id, {}, {}) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
