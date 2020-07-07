package com.example.chotuvemobileapp.data.videos

import com.example.chotuvemobileapp.entities.CommentItem

class VideoInfo(
    val description: String,
    val reaction: String?,
    val likes: Int,
    val dislikes: Int,
    val comments: ArrayList<CommentItem>
)