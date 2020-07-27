package com.example.chotuvemobileapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chotuvemobileapp.data.repositories.RevertPassDataSource
import com.example.chotuvemobileapp.helpers.Utilities
import kotlinx.android.synthetic.main.activity_revert_password.*


class RevertPasswordActivity : AppCompatActivity() {
    private val email by lazy {
        RevertPassEmail.text.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_revert_password)

        RevertPassProgressBar.visibility = View.GONE

        SendMeInstructionsBtn.setOnClickListener {
            // TODO: * Validar email.
            showLoadingScreen()
            RevertPassDataSource.sendEmail(email) {
                when (it) {
                    Utilities.SUCCESS_MESSAGE -> {
                        clearLoadingScreen()
                        Toast.makeText(applicationContext, "Success email send", Toast.LENGTH_LONG).show()

                        goToPassConfiguration()
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

    private fun goToPassConfiguration() {
        val passConfigIntent = Intent(this, PasswordConfigActivity::class.java)
        passConfigIntent.putExtra(Utilities.EMAIL, email)
        startActivity(passConfigIntent)
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