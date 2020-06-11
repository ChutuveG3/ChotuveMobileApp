package com.example.chotuvemobileapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chotuvemobileapp.data.users.LoginDataSource
import com.example.chotuvemobileapp.data.users.User
import com.example.chotuvemobileapp.helpers.Utilities.createDatePicker
import com.example.chotuvemobileapp.helpers.Utilities.watchText
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.time.LocalDate

class SignUpActivity : AppCompatActivity() {
    private val registerInfo by lazy {
        User(
            RegNameText.text.toString(),
            RegLastNameText.text.toString(),
            RegEmailText.text.toString(),
            RegPwFirstText.text.toString(),
            RegUsernameText.text.toString(),
            RegDateText.text.toString()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        createDatePicker(RegDateText, this)

        SignupProgressBar.visibility = View.GONE

        SignUpButton.isEnabled = false
        SignUpButton.alpha = .5f

        RegUsernameText.watchText(SignUpButton, this::isDataCorrect)
        RegNameText.watchText(SignUpButton, this::isDataCorrect)
        RegLastNameText.watchText(SignUpButton, this::isDataCorrect)
        RegEmailText.watchText(SignUpButton, this::isDataCorrect)
        RegPwFirstText.watchText(SignUpButton, this::isDataCorrect)
        RegPwSecondText.watchText(SignUpButton, this::isDataCorrect)

        SignUpButton.setOnClickListener{
            if (isDataValid()) {
                SignupScreen.alpha = .2F
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                SignupProgressBar.visibility = View.VISIBLE

                LoginDataSource.addUser(registerInfo){
                    when (it) {
                        "Failure" -> Toast.makeText(applicationContext, getString(R.string.request_failure), Toast.LENGTH_LONG).show()
                        "Success" -> {
                            goToLogin(registerInfo)
                        }
                        "user_name_already_exists" -> RegUsername.error = getString(R.string.user_taken)
                        "user_email_already_exists" -> RegEmail.error = getString(R.string.email_taken)
                        else -> Toast.makeText( applicationContext, getString(R.string.internal_error), Toast.LENGTH_LONG).show()
                    }
                    SignupScreen.alpha = 1F
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    SignupProgressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun goToLogin(registerInfo: User) {
        startActivity(Intent(this, LoginActivity::class.java))
        val nameToShow = registerInfo.first_name
        Toast.makeText(
            applicationContext, "Welcome, $nameToShow! \nNow please sign in",
            Toast.LENGTH_LONG
        ).show()
        finish()
    }
    private fun isDataValid(): Boolean{
        var valid = true

        RegUsername.error = null
        RegEmail.error = null
        RegPassFirst.error = null
        RegDate.error = null

        if (RegPwFirstText.text.toString().length < 6){
            RegPassFirst.error = getString(R.string.invalid_pass)
            valid = false
        }
        if (RegPwFirstText.text.toString() != RegPwSecondText.text.toString()){
            RegPassSecond.error = getString(R.string.password_mismatch)
            valid = false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(RegEmailText.text.toString()).matches() ||
                RegEmailText.length() < 5){
            RegEmail.error = getString(R.string.invalid_email)
            valid = false
        }
        if (LocalDate.parse(RegDateText.text.toString()) >= LocalDate.now()){
            RegDate.error = getString(R.string.invalid_date)
            valid = false
        }
        return valid
    }
    private fun isDataCorrect(): Boolean {
        var correct = true

        val name = RegNameText.text.toString()
        val lastName = RegLastNameText.text.toString()
        val username = RegUsernameText.text.toString()
        val email = RegEmailText.text.toString()
        val password = RegPwFirstText.text.toString()

        if (name.isEmpty()){
            correct = false
        }
        if (lastName.isEmpty()){
            correct = false
        }
        if (username.isEmpty()){
            correct = false
        }
        if (email.isEmpty()){
            correct = false
        }
        if (password.isEmpty()){
            correct = false
        }
        return correct
    }
}


