package com.example.chotuvemobileapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chotuvemobileapp.data.users.LoginDataSource
import com.example.chotuvemobileapp.helpers.Utilities.watchText
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        GoToSignUpBtn.setOnClickListener { startActivity(Intent(this, SignUpActivity::class.java)) }

        SignInButton.isEnabled = false
        SignInButton.alpha = .5f

        LoginProgressBar.visibility = View.GONE

        LogInUsername.watchText(SignInButton, this::isDataCorrect)
        LoginPassword.watchText(SignInButton, this::isDataCorrect)

        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener(this@LoginActivity) { instanceIdResult ->
                val newToken = instanceIdResult.token
                Log.i("FIREBASE_TOKEN", newToken)
            }

        SignInButton.setOnClickListener {
            if(isDataValid()) {
                LoginScreen.alpha = .2F
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                LoginProgressBar.visibility = View.VISIBLE

                LoginDataSource.login(LogInUsername.text.toString(), LoginPassword.text.toString()) {
                    when (it) {
                        "Failure" -> Toast.makeText(applicationContext, getString(R.string.internal_error), Toast.LENGTH_LONG).show()
                        "InvalidParams" -> showInvalidUsername()
                        else -> saveDataAndStartHome(it)
                    }
                    LoginScreen.alpha = 1F
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    LoginProgressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun saveDataAndStartHome(it: String) {
        val preferences = applicationContext.getSharedPreferences(
            getString(R.string.shared_preferences_file),
            Context.MODE_PRIVATE
        ).edit()
        preferences.putString("token", it)
        preferences.putString("username", LogInUsername.text.toString())
        preferences.putString("password", LoginPassword.text.toString())
        preferences.apply()
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun showInvalidUsername() {
        UsernameInput.error = getString(R.string.failed_login)
        PasswordInput.error = getString(R.string.failed_login)
        PasswordInput.getChildAt(1).visibility = View.GONE
    }

    private fun isDataCorrect(): Boolean {
        UsernameInput.error = null
        PasswordInput.error = null
        return LogInUsername.text.toString().isNotEmpty() && LoginPassword.text.toString().isNotEmpty()
    }

    private fun isDataValid(): Boolean{
        var valid = true

        UsernameInput.error = null
        PasswordInput.error = null

        if (LoginPassword.text.toString().length < 6){
            PasswordInput.error = getString(R.string.invalid_pass)
            valid = false
        }
        if (LogInUsername.length() > 30){
            UsernameInput.error = getString(R.string.invalid_username)
            valid = false
        }
        return valid
    }
}
