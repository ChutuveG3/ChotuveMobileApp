package com.example.chotuvemobileapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chotuvemobileapp.data.Error
import com.example.chotuvemobileapp.data.LoginDataSource.getUsersName
import com.example.chotuvemobileapp.data.LoginDataSource.login
import kotlinx.android.synthetic.main.activity_login.*

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
            val username = LogInUsername.text.toString()
            val result = login(username, LoginPassword.text.toString())
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            /*if (result.Success){
                startActivity(Intent(this, MainActivity::class.java))
                val nameToShow = getUsersName(username)
                Toast.makeText(applicationContext, "Welcome, $nameToShow!", Toast.LENGTH_LONG).show()
                finish()
            }

            else if (result.Error == Error.UserNotRegistered) UsernameInput.error = getString(R.string.invalid_username)
            else PasswordInput.error = getString(R.string.invalid_password)*/
        }
    }
    private fun EditText.watchText() {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                SignInButton.isEnabled = isDataValid()
                if (SignInButton.isEnabled) SignInButton.alpha = 1f
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    fun isDataValid(): Boolean {
        UsernameInput.error = null
        PasswordInput.error = null
        return LogInUsername.text.toString().isNotEmpty() && LoginPassword.text.toString().isNotEmpty()
    }
}


