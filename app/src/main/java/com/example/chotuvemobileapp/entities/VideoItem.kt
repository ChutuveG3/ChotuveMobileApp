package com.example.chotuvemobileapp.entities

import com.google.gson.annotations.SerializedName
import java.io.FileDescriptor


class VideoItem(
    @SerializedName("title")
    val title: String,
    @SerializedName("owner")
    val user: String,
    @SerializedName("datetime")
    val date: String,
    @SerializedName("downlad_url")
    val url: String,
    @SerializedName("description")
    val description: String
)