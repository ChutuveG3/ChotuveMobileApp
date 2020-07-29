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
import com.example.chotuvemobileapp.helpers.Utilities.FAILURE_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.INVALID_PARAMS_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.SUCCESS_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.watchText
import kotlinx.android.synthetic.main.activity_revert_password.*


class RevertPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_revert_password)

        RevertPassProgressBar.visibility = View.GONE
        RevertPassEmail.watchText(SendMeInstructionsBtn, this::isEmailValid)

        SendMeInstructionsBtn.setOnClickListener {
            showLoadingScreen()
            RevertPassDataSource.sendEmail(RevertPassEmail.text.toString()) {
                when (it) {
                    SUCCESS_MESSAGE -> {
                        clearLoadingScreen()
                        Toast.makeText(applicationContext, "Email sent, check your inbox", Toast.LENGTH_LONG).show()
                        goToPassConfiguration()
                    }
                    INVALID_PARAMS_MESSAGE -> {
                        clearLoadingScreen()
                        EmailInput.error = "Email not registered"
                    }
                    FAILURE_MESSAGE -> {
                        clearLoadingScreen()
                        Toast.makeText(applicationContext, getString(R.string.request_failure), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun goToPassConfiguration() {
        val passConfigIntent = Intent(this, PasswordConfigActivity::class.java)
        passConfigIntent.putExtra(Utilities.EMAIL, RevertPassEmail.text.toString())
        startActivity(passConfigIntent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
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

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun isEmailValid(): Boolean = android.util.Patterns.EMAIL_ADDRESS.matcher(RevertPassEmail.text.toString()).matches()
}