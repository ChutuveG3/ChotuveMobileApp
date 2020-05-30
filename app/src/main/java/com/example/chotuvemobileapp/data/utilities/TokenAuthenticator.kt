package com.example.chotuvemobileapp.data.utilities

import android.content.SharedPreferences
import com.example.chotuvemobileapp.data.response.LoginResponse
import com.example.chotuvemobileapp.data.utilities.HttpUtilities.buildClient
import com.google.gson.Gson
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(private val preferences: SharedPreferences) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val updatedToken = getUpdatedToken()
        if (updatedToken != null) {
            preferences.edit().putString("token", updatedToken).apply()
            return response.request.newBuilder().addHeader("authorization", updatedToken).build()
        }
        return null
    }

    private fun getUpdatedToken(): String?{
        val email = preferences.getString("email", "")!!
        val pass = preferences.getString("password", "")!!
        val retrofit = buildClient()
        val response = retrofit.loginUser(email, pass).execute()
        if (response.isSuccessful){
            val resp = Gson().fromJson(response.body()!!.string(), LoginResponse::class.java)
            return resp.token
        }
        return null
    }
}