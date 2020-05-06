package com.example.chotuvemobileapp.data

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.util.*

data class User(
    @SerializedName("first_name")
    val first_name: String,
    @SerializedName("last_name")
    val last_name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("user_name")
    val user_name: String,
    @SerializedName("birthdate")
    val birthdate: String
)