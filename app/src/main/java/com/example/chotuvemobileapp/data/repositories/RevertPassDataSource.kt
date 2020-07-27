package com.example.chotuvemobileapp.data.repositories

import com.example.chotuvemobileapp.data.requests.RevertPassRequest
import com.example.chotuvemobileapp.data.utilities.HttpUtilities
import com.example.chotuvemobileapp.helpers.Utilities.FAILURE_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.INVALID_PARAMS_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.SUCCESS_MESSAGE
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object RevertPassDataSource {
    fun sendEmail(email: String, myCallback: (String) -> Unit){

        val retrofit = HttpUtilities.buildClient()
        val request = RevertPassRequest(email)

        retrofit.passwordRecovery(request).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) = myCallback.invoke(FAILURE_MESSAGE)

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                when {
                    response.isSuccessful -> myCallback.invoke(SUCCESS_MESSAGE)
                    response.code() == 409 -> myCallback.invoke(INVALID_PARAMS_MESSAGE)
                    else ->  myCallback.invoke(FAILURE_MESSAGE)
                }
            }
        })
    }
}