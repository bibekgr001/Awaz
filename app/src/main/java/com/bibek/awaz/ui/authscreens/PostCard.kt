package com.bibek.awaz.ui.authscreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bibek.awaz.viewmodel.Post
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PostCard(
    post: Post,
    onLike: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = (onClick != null)) { onClick?.invoke() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = post.email, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = post.caption, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val time = post.timestamp?.toDate()?.let { sdf.format(it) } ?: "unknown"
                Text(text = time, style = MaterialTheme.typography.bodySmall)

                Row {
                    Text(text = "Likes: ${post.likes}", modifier = Modifier.alignByBaseline())
                    Spacer(modifier = Modifier.width(8.dp))
                    if (onLike != null) {
                        TextButton(onClick = { onLike() }) { Text("Like") }
                    }
                }
            }
        }
    }
}
