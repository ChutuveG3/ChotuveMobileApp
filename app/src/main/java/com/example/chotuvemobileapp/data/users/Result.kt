package com.example.chotuvemobileapp.data.users


enum class Error {
    UserNotRegistered,
    IncorrectPassword
}
class Result (val Success: Boolean, val Error : Error?)
