package com.example.chotuvemobileapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chotuvemobileapp.data.repositories.LoginDataSource
import com.example.chotuvemobileapp.data.users.User
import com.example.chotuvemobileapp.helpers.Utilities.FAILURE_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.INVALID_PARAMS_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.SUCCESS_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.USERNAME
import com.example.chotuvemobileapp.helpers.Utilities.createDatePicker
import com.example.chotuvemobileapp.helpers.Utilities.watchText
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.time.LocalDate

class SignUpActivity : AppCompatActivity() {
    private val registerInfo by lazy {
        User(
            RegNameText.text.toString(),
            RegLastNameText.text.toString(),
            RegEmailText.text.toString(),
            RegPwFirstText.text.toString(),
            null,
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
                showLoadingScreen()
                LoginDataSource.addUser(registerInfo){
                    when (it) {
                        FAILURE_MESSAGE -> {
                            clearLoadingScreen()
                            Toast.makeText(applicationContext, getString(R.string.request_failure), Toast.LENGTH_LONG).show()
                        }
                        SUCCESS_MESSAGE -> goToLogin(registerInfo)
                        "user_name_already_exists" -> {
                            RegUsername.error = getString(R.string.user_taken)
                            clearLoadingScreen()
                        }
                        "user_email_already_exists" ->{
                            RegEmail.error = getString(R.string.email_taken)
                            clearLoadingScreen()
                        }
                        else -> {
                            Toast.makeText( applicationContext, getString(R.string.internal_error), Toast.LENGTH_LONG).show()
                            clearLoadingScreen()
                        }
                    }
                }
            }
        }
    }

    private fun clearLoadingScreen() {
        SignupScreen.alpha = 1F
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        SignupProgressBar.visibility = View.GONE
    }

    private fun showLoadingScreen() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        SignupScreen.alpha = .2F
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        SignupProgressBar.visibility = View.VISIBLE
    }

    private fun goToLogin(registerInfo: User) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener(this) { instanceIdResult ->
                val newToken = instanceIdResult.token
                LoginDataSource.tokenLogin(registerInfo.user_name, registerInfo.password!!, newToken) {
                    when (it) {
                        FAILURE_MESSAGE, INVALID_PARAMS_MESSAGE -> Toast.makeText(applicationContext, getString(R.string.internal_error), Toast.LENGTH_LONG).show()
                        else -> {
                            val preferences = applicationContext.getSharedPreferences(
                                getString(R.string.shared_preferences_file),
                                Context.MODE_PRIVATE
                            ).edit()
                            preferences.putString("token", newToken)
                            preferences.putString(USERNAME, registerInfo.user_name)
                            preferences.putString("password", registerInfo.password)
                            preferences.apply()
                            startActivity(Intent(this, HomeActivity::class.java))
                            Toast.makeText(
                                applicationContext, "Welcome, ${registerInfo.first_name}!",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    }
                    clearLoadingScreen()
                }
            }.addOnFailureListener { Toast.makeText(applicationContext, R.string.internal_error, Toast.LENGTH_LONG).show() }
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
        if (RegUsernameText.length() > 30) {
            RegEmail.error = getString(R.string.invalid_username)
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


