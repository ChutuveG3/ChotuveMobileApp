package com.example.chotuvemobileapp.data.videos

import com.example.chotuvemobileapp.entities.CommentItem

class VideoInfo(
    val description: String,
    var reaction: String?,
    var likes: Int,
    var dislikes: Int,
    val views: Int,
    val comments: ArrayList<CommentItem>?
)