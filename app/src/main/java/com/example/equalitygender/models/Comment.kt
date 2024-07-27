package com.example.equalitygender.models

data class Comment(
    var id: String = "",
    var postId: String = "",
    var userId: String = "",
    var userName: String = "",
    var text: String = "",
    var timestamp: Long = 0
)
