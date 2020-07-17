package com.example.chotuvemobileapp.data.users

data class User(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String?,
    val firebase_token: String?,
    val user_name: String,
    val birthdate: String
)