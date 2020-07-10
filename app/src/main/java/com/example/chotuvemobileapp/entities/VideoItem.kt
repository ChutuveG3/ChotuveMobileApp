package com.example.chotuvemobileapp.entities

import com.google.gson.annotations.SerializedName

class VideoItem(
    val id: String,
    val title: String,
    @SerializedName("owner")
    val user: String,
    @SerializedName("datetime")
    val date: String,
    val url: String
)