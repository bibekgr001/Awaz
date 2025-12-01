package com.bibek.awaz.ui.authscreens

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bibek.awaz.viewmodel.Post
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun PostItem(
    post: Post,
    onClick: (Post) -> Unit = {},
    onLike: () -> Unit = {},
    onDelete: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutine = rememberCoroutineScope()

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(post) },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = post.email, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = post.caption, style = MaterialTheme.typography.bodyLarge)
                }

                Column(horizontalAlignment = Alignment.End) {

                    // Play Audio
                    TextButton(
                        onClick = {
                            val url = post.audioUrl
                            if (url.isNullOrBlank()) {
                                coroutine.launch { snackbarHostState.showSnackbar("No audio attached") }
                                return@TextButton
                            }

                            if (isPlaying) {
                                mediaPlayer?.stop()
                                mediaPlayer?.release()
                                mediaPlayer = null
                                isPlaying = false
                            } else {
                                try {
                                    val mp = MediaPlayer()
                                    mp.setAudioAttributes(
                                        AudioAttributes.Builder()
                                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                            .build()
                                    )
                                    mp.setDataSource(url)
                                    mp.prepareAsync()
                                    mp.setOnPreparedListener {
                                        it.start()
                                        isPlaying = true
                                    }
                                    mp.setOnCompletionListener {
                                        it.release()
                                        isPlaying = false
                                    }
                                    mediaPlayer = mp
                                } catch (e: Exception) {
                                    coroutine.launch { snackbarHostState.showSnackbar("Cannot play audio") }
                                }
                            }
                        }
                    ) {
                        Text(if (isPlaying) "Stop" else "Play")
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Like
                    TextButton(onClick = onLike) { Text("Like (${post.likes})") }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Delete (only owner sees it)
                    if (post.uid == currentUid) {
                        TextButton(onClick = { onDelete(post.id) }) {
                            Text("Delete", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Snackbar host (per item) so messages show
            SnackbarHost(hostState = snackbarHostState)
        }
    }
}
