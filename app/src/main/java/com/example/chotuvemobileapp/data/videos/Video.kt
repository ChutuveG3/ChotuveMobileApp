package com.example.chotuvemobileapp.data.videos

import android.net.Uri
import com.google.gson.annotations.SerializedName
import java.time.format.DateTimeFormatter

data class Video(
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("visibility")
    val visibility: String,
    @SerializedName("download_url")
    val firebaseUri: String,
    @SerializedName("datetime")
    val uploadDatetime: String,
    @SerializedName("file_name")
    val fileName: String,
    @SerializedName("file_size")
    val fileSize: Long
    )