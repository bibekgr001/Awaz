package com.bibek.awaz.ui.authscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.bibek.awaz.viewmodel.AuthViewModel
import com.bibek.awaz.viewmodel.Post
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: AuthViewModel) {

    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid ?: ""
    val email = user?.email ?: "Unknown"

    var posts by remember { mutableStateOf(listOf<Post>()) }

    LaunchedEffect(Unit) {
        viewModel.listenToPosts(
            onUpdate = { list -> posts = list.filter { it.uid == uid } },
            onError = {}
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Profile") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = email.first().uppercase(),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(email, style = MaterialTheme.typography.titleMedium)
                    Text("${posts.size} posts", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("My Posts", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(posts) { post ->
                    PostCard(post)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
