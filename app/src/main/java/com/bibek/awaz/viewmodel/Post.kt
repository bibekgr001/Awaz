package com.bibek.awaz.viewmodel

import com.google.firebase.Timestamp

data class Post(
    val id: String = "",
    val uid: String = "",
    val email: String = "",
    val caption: String = "",
    val audioUrl: String? = null,     // optional audio url
    val timestamp: Timestamp? = null,
    val likes: Long = 0L
)
