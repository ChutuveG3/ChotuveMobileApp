package com.example.chotuvemobileapp.data.utilities

import android.content.SharedPreferences
import com.example.chotuvemobileapp.data.requests.LoginRequest
import com.example.chotuvemobileapp.data.requests.ThirdPartyLoginRequest
import com.example.chotuvemobileapp.data.response.LoginResponse
import com.example.chotuvemobileapp.data.utilities.HttpUtilities.buildClient
import com.example.chotuvemobileapp.helpers.Utilities.FIREBASE_AUTH_TOKEN
import com.example.chotuvemobileapp.helpers.Utilities.THIRD_PARTY_LOGIN
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
            return response.request.newBuilder().removeHeader("authorization").addHeader("authorization", updatedToken).build()
        }
        return null
    }

    private fun getUpdatedToken(): String?{

        val retrofit = buildClient()

        val method =
            if (preferences.getBoolean(THIRD_PARTY_LOGIN,false))
                retrofit.loginThirdParty(ThirdPartyLoginRequest(
                    preferences.getString(FIREBASE_AUTH_TOKEN, "")!!,
                    null
                ))
            else
                retrofit.loginUser(LoginRequest(
                    preferences.getString("username", "")!!,
                    preferences.getString("password", "")!!
                ))
        var response = method.execute()
        if (response.isSuccessful){
            val resp = Gson().fromJson(response.body()!!.string(), LoginResponse::class.java)
            return resp.token
        }
        return null
    }
}