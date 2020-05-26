package com.example.chotuvemobileapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chotuvemobileapp.data.users.LoginDataSource
import com.example.chotuvemobileapp.data.users.User
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.text.DecimalFormat
import java.time.LocalDate
import java.util.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        RegDateText.setOnClickListener {
            val dialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{ _, mYear, mMonth, mDay ->
                val df = DecimalFormat("00")
                val trueMonth = df.format(mMonth + 1)
                val trueDay = df.format(mDay)
                val text = "$mYear-$trueMonth-$trueDay"
                RegDateText.setText(text)

            }, year, month, day)
            dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
            dialog.show()
        }

        SignupProgressBar.visibility = View.GONE

        SignUpButton.isEnabled = false
        SignUpButton.alpha = .5f

        RegUsernameText.watchText()
        RegNameText.watchText()
        RegLastNameText.watchText()
        RegEmailText.watchText()
        RegPwFirstText.watchText()
        RegPwSecondText.watchText()

        SignUpButton.setOnClickListener{
            if (isDataValid()) {
                SignupScreen.alpha = .2F
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                SignupProgressBar.visibility = View.VISIBLE

                val registerInfo = User(
                    RegNameText.text.toString(),
                    RegLastNameText.text.toString(),
                    RegEmailText.text.toString(),
                    RegPwFirstText.text.toString(),
                    RegUsernameText.text.toString(),
                    RegDateText.text.toString()
                )

                LoginDataSource.addUser(registerInfo)  {
                    when (it) {
                        "Failure" -> {
                            Toast.makeText(applicationContext, getString(R.string.request_failure),
                                Toast.LENGTH_LONG).show()
                        }
                        "Success" -> {
                            startActivity(Intent(this, LoginActivity::class.java))
                            val nameToShow = RegNameText.text.toString()
                            Toast.makeText(applicationContext,"Welcome, $nameToShow! \n Now please sign in",
                                Toast.LENGTH_LONG).show()
                            finish()
                        }
                        "user_name_already_exists" -> {
                            RegUsername.error = getString(R.string.user_taken)
                        }
                        "user_email_already_exists" -> {
                            RegEmail.error = getString(R.string.email_taken)
                        }
                        else -> {
                            Toast.makeText( applicationContext, getString(R.string.internal_error),
                                Toast.LENGTH_LONG).show()
                        }
                    }
                    SignupScreen.alpha = 1F
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    SignupProgressBar.visibility = View.GONE
                }

            }
        }
    }

    private fun EditText.watchText() {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                SignUpButton.isEnabled = isDataCorrect()
                if (SignUpButton.isEnabled) SignUpButton.alpha = 1f
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
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
    fun isDataCorrect(): Boolean {
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


