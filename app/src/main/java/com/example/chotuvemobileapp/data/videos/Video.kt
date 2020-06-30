package com.example.chotuvemobileapp.data.videos

import com.google.gson.annotations.SerializedName

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
    val fileSize: String,
    val latitude: Double?,
    val longitude: Double?
)