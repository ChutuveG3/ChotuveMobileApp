package com.example.chotuvemobileapp

import com.google.gson.Gson
import okhttp3.*


/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
object LoginDataSource {

    private val users = HashMap<String, User>()
    private val client = OkHttpClient()

    fun login(username: String, password: String): Result {
        if (!users.containsKey(username)) return Result(false, Error.UserNotRegistered)
        val user = users[username]
        if (user!!.password != password) return Result(false, Error.IncorrectPassword)

        return Result(true, null)
    }

    fun getUsersName(username: String): String{
        return users[username]!!.first_name
    }



    fun addUser(user: User): String {
        val formBody = FormBody.Builder().add("first_name", user.first_name)
            .add("last_name", user.last_name)
            .add("email", user.email)
            .add("password", user.password)
            .add("user_name", user.user_name)
            .add("birthdate", user.birthdate).build()
        val request =
            Request.Builder().url("https://chotuve-app-server-develop.herokuapp.com/users")
                .post(formBody).build()

        val response = client.newCall(request).execute()
        return if (!response.isSuccessful) {
            val body = response.body!!.string()
            if (response.code == 400) {
                Gson().fromJson(body, ResponseBody::class.java).internal_code
            } else {
                body
            }
        } else {
            ""
        }
    }
}


