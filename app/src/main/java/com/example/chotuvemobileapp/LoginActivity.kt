package com.example.chotuvemobileapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chotuvemobileapp.data.users.LoginDataSource
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.time.LocalDate

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        GoToSignUpBtn.setOnClickListener { startActivity(Intent(this, SignUpActivity::class.java)) }

        SignInButton.isEnabled = false
        SignInButton.alpha = .5f

        LogInUsername.watchText()
        LoginPassword.watchText()

        SignInButton.setOnClickListener {

            if(isDataValid()) {
                LoginDataSource.login(
                    LogInUsername.text.toString(),
                    LoginPassword.text.toString()
                ) {
                    when (it) {
                        "Success" -> {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }
                        "InvalidParams" -> {
                            UsernameInput.error = getString(R.string.failed_login)
                            PasswordInput.error = getString(R.string.failed_login)
                            PasswordInput.getChildAt(1).visibility = View.GONE
                        }
                        else -> {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.internal_error),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }

        }
    }
    private fun EditText.watchText() {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                SignInButton.isEnabled = isDataCorrect()
                if (SignInButton.isEnabled) SignInButton.alpha = 1f
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    fun isDataCorrect(): Boolean {
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
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(LogInUsername.text.toString()).matches() ||
            LogInUsername.length() < 5){
            UsernameInput.error = getString(R.string.invalid_email)
            valid = false
        }
        return valid
    }
}


