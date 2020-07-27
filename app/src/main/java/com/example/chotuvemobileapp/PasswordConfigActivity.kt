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
import com.example.chotuvemobileapp.helpers.Utilities.EMAIL
import com.example.chotuvemobileapp.helpers.Utilities.INVALID_PARAMS_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.SUCCESS_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.watchText
import kotlinx.android.synthetic.main.activity_password_config.*

class PasswordConfigActivity : AppCompatActivity() {
    private val email by lazy {
        intent.getStringExtra(EMAIL)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_config)

        val message = "We sent an email to $email, please insert the code you received below:"

        PassConfigProgressBar.visibility = View.GONE
        PassConfigDescriptionText.text = message

        PassConfigCode.watchText(ConfigPassConfirmBtn, this::isDataValid)
        PassConfigPassword.watchText(ConfigPassConfirmBtn, this::isDataValid)
        PassConfigConfirmCPassword.watchText(ConfigPassConfirmBtn, this::isDataValid)

        ConfigPassConfirmBtn.setOnClickListener {
            showLoadingScreen()
            RevertPassDataSource.changePassword(PassConfigCode.text.toString(), PassConfigPassword.text.toString()){
                when (it){
                    SUCCESS_MESSAGE ->{
                        startActivity(Intent(this, LoginActivity::class.java))
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        finish()
                    }
                    INVALID_PARAMS_MESSAGE ->{
                        clearLoadingScreen()
                        CodeInput.error = "Invalid code"
                    }
                    else -> {
                        clearLoadingScreen()
                        Toast.makeText(applicationContext, R.string.internal_error, Toast.LENGTH_LONG).show()
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
        PassConfigScreen.alpha = .2F
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        PassConfigProgressBar.visibility = View.VISIBLE
    }
    private fun clearLoadingScreen() {
        PassConfigScreen.alpha = 1F
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        PassConfigProgressBar.visibility = View.GONE
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun isDataValid(): Boolean =
        PassConfigCode.text.toString().length == 6 &&
                PassConfigPassword.text.toString().isNotBlank() &&
                PassConfigConfirmCPassword.text.toString().isNotBlank() &&
                PassConfigPassword.text.toString().length >= 6 &&
                PassConfigPassword.text.toString() == PassConfigConfirmCPassword.text.toString()
}