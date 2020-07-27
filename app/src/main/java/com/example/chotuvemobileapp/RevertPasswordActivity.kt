package com.example.chotuvemobileapp

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chotuvemobileapp.data.repositories.RevertPassDataSource
import com.example.chotuvemobileapp.helpers.Utilities
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_revert_password.*


class RevertPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_revert_password)

//        SendMeInstructionsBtn.isEnabled = false
//        SendMeInstructionsBtn.alpha = .5f
        RevertPassProgressBar.visibility = View.GONE

        SendMeInstructionsBtn.setOnClickListener {
            // TODO: * Validar email.
            val email = RevertPassEmail.text.toString()

            showLoadingScreen()
            RevertPassDataSource.sendEmail(email) {
                when (it) {
                    Utilities.SUCCESS_MESSAGE -> {
                        // GOTO: Pantalla para ingresar el token (6 caracteres) + pass y confirm pass.
                        clearLoadingScreen()
                        Toast.makeText(applicationContext, "Success email send", Toast.LENGTH_LONG)
                    }
                    Utilities.FAILURE_MESSAGE -> {
                        clearLoadingScreen()
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.request_failure),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun showLoadingScreen() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
        RevertPassScreen.alpha = .2F
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        RevertPassProgressBar.visibility = View.VISIBLE
    }
    private fun clearLoadingScreen() {
        RevertPassScreen.alpha = 1F
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        RevertPassProgressBar.visibility = View.GONE
    }


}