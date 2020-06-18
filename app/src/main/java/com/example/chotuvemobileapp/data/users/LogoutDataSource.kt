package com.example.chotuvemobileapp.data.users
import android.util.Log
import com.example.chotuvemobileapp.data.response.AuthErrorResponse
import com.example.chotuvemobileapp.data.response.ErrorMessage
import com.example.chotuvemobileapp.data.utilities.HttpUtilities.buildClient
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object LogoutDataSource {
    fun logout(username: String, myCallback: (String) -> Unit) {
        val retrofit = buildClient()

        retrofit.logoutUser(username).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                myCallback.invoke("Failure")
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> {
                        myCallback.invoke("Success")
                    }
                    else -> {
                        val error = Gson().fromJson(response.errorBody()!!.string(),
                            AuthErrorResponse::class.java)
                        Log.d("LOGOUT_ERROR", error.message.toString())
                        myCallback.invoke("Error")
                    }
                }
            }
        })
    }
}
