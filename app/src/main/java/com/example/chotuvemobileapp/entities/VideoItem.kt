package com.example.chotuvemobileapp.entities

import com.google.gson.annotations.SerializedName
import java.io.FileDescriptor


class VideoItem(
    val title: String,
    @SerializedName("owner")
    val user: String,
    @SerializedName("datetime")
    val date: String,
    val url: String,
    val description: String,
    val visibility: String
)