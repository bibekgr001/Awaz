package com.bibek.awaz.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private var postsListener: ListenerRegistration? = null

    // ⭐ NEW — expose current user to UI
    val currentUser get() = auth.currentUser

    // LOGIN
    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Login failed") }
    }

    // REGISTER
    fun register(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                val userData = hashMapOf(
                    "uid" to uid,
                    "email" to email,
                    "createdAt" to Timestamp.now()
                )
                firestore.collection("users")
                    .document(uid)
                    .set(userData)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it.message ?: "Failed to save user info") }
            }
            .addOnFailureListener { onFailure(it.message ?: "Registration failed") }
    }

    // GET user info
    fun getUserData(
        uid: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val email = document.getString("email") ?: "Unknown user"
                    onSuccess(email)
                } else {
                    onFailure("User not found")
                }
            }
            .addOnFailureListener { onFailure(it.message ?: "Failed to load user data") }
    }

    // CREATE POST
    fun createPost(
        caption: String,
        audioUrl: String? = null,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val user = auth.currentUser ?: return onFailure("Not signed in")

        val post = hashMapOf(
            "uid" to user.uid,
            "email" to (user.email ?: "unknown"),
            "caption" to caption,
            "audioUrl" to audioUrl,
            "timestamp" to Timestamp.now(),
            "likes" to 0L
        )

        firestore.collection("voice_posts")
            .add(post)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Failed to create post") }
    }

    // LISTEN to posts
    fun listenToPosts(onUpdate: (List<Post>) -> Unit, onError: (String) -> Unit) {
        postsListener?.remove()
        postsListener = firestore.collection("voice_posts")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error.message ?: "Unknown error")
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.map { doc ->
                    Post(
                        id = doc.id,
                        uid = doc.getString("uid") ?: "",
                        email = doc.getString("email") ?: "",
                        caption = doc.getString("caption") ?: "",
                        audioUrl = doc.getString("audioUrl"),
                        timestamp = doc.getTimestamp("timestamp"),
                        likes = (doc.getLong("likes") ?: 0L)
                    )
                } ?: emptyList()

                onUpdate(list)
            }
    }

    fun toggleLike(postId: String) {
        val docRef = firestore.collection("voice_posts").document(postId)
        firestore.runTransaction { tx ->
            val snapshot = tx.get(docRef)
            val current = snapshot.getLong("likes") ?: 0L
            tx.update(docRef, "likes", current + 1L)
            null
        }
    }

    fun deletePost(postId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        firestore.collection("voice_posts")
            .document(postId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Failed to delete post") }
    }

    override fun onCleared() {
        postsListener?.remove()
        super.onCleared()
    }
}
