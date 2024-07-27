package com.example.equalitygender.models

data class Post(
    var id: String = "",
    var isi: String = "",
    var imageUrl: String = "",
    var upvotes: Int = 0,
    var downvotes: Int = 0,
    var timestamp: Long = 0,
    var userId: String = "",
    var votes: MutableMap<String, Int> = mutableMapOf(), // Key is user ID, value is vote type (1 for upvote, -1 for downvote)
    @Transient var username: String = "", // Transient property for username
    @Transient var commentsCount: Int = 0 // Transient property for comments count
)
