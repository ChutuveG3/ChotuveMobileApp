package com.example.chotuvemobileapp.data


enum class Error {
    UserNotRegistered,
    IncorrectPassword
}
class Result (val Success: Boolean, val Error : Error?)
