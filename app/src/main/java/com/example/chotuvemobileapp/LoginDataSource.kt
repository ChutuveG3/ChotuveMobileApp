package com.example.chotuvemobileapp

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
object LoginDataSource {

    private val users = HashMap<String, User>()

    init {
        fillUsers()
    }

    fun login(username: String, password: String): Result {
        if (!users.containsKey(username)) return Result(false, Error.UserNotRegistered)
        val user = users[username]
        if (user!!.password != password) return Result(false, Error.IncorrectPassword)

        return Result(true, null)
    }

    fun getUsersName(username: String): String{
        return users[username]!!.firstName
    }

    fun fillUsers(){
        addUser("usuario", "Nombre", "Apellido", "Mail", "Password", "Fecha")
        addUser("usuario2", "Nombre3", "Apellido463", "Mailvx", "Password241", "Fecha352")
    }


    fun addUser(username: String, firstName: String, lastName: String, email: String, password: String, dateOfBirth: String){
        val newUser = User(
            firstName,
            lastName,
            email,
            password,
            dateOfBirth
        )
        users[username] = newUser
    }

    fun userExists(username: String): Boolean{
        return users.containsKey(username)
    }
}

