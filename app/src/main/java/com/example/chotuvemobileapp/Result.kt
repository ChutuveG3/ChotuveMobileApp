package com.example.chotuvemobileapp


enum class Error {
    UserNotRegistered,
    IncorrectPassword
}
class Result (val Success: Boolean, val Error : Error?)
