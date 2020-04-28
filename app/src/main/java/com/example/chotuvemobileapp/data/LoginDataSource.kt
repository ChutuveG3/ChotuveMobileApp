package com.example.chotuvemobileapp.data

import com.example.chotuvemobileapp.data.model.LoggedInUser
import com.example.chotuvemobileapp.data.model.User
import java.io.IOException
import java.time.LocalDate
import java.util.*

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
object LoginDataSource {

    private val users = hashMapOf<String, User>()

    fun login(username: String, password: String): Result<LoggedInUser> {
        if (!users.containsKey(username)) return Result.Error(IOException("User doesn't exist"))

        val user = users[username]
        if (user!!.password != password) Result.Error(IOException("Incorrect password"))
        val loggedUser = LoggedInUser(username, user.firstName)
        return Result.Success(loggedUser)
    }

    fun logout() {
    }

    fun addUser(username: String, firstName: String, lastName: String, email: String, password: String, dateOfBirth: String){
        val newUser = User(firstName, lastName, email, password, dateOfBirth)
        users[username] = newUser
    }

    fun userExists(username: String): Boolean{
        return users.containsKey(username)
    }
}

