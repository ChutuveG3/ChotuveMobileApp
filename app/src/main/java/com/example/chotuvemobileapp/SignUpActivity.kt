package com.example.chotuvemobileapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chotuvemobileapp.LoginDataSource.addUser
import com.example.chotuvemobileapp.LoginDataSource.userExists
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
                val month = df.format(mMonth + 1)
                val text = "$mDay/$month/$mYear"
                RegDateText.setText(text)

            }, year, month, day)
            dialog.show()
        }

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
                addUser(
                    RegUsernameText.text.toString(), RegNameText.text.toString(),
                    RegLastNameText.text.toString(), RegEmailText.text.toString(),
                    RegPwFirstText.text.toString(), RegDateText.text.toString()
                )
                startActivity(Intent(this, MainActivity::class.java))
                val nameToShow = RegNameText.text.toString()
                Toast.makeText(applicationContext, "Welcome, $nameToShow!", Toast.LENGTH_LONG)
                    .show()
                finish()
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

    fun isDataValid(): Boolean{
        var valid = true

        RegUsername.error = null
        RegEmail.error = null
        RegPassFirst.error = null
        RegDate.error = null

        if (userExists(RegUsernameText.text.toString())){
            RegUsername.error = getString(R.string.user_taken)
            valid = false
        }
        if (RegPwFirstText.text.toString() != RegPwSecondText.text.toString()){
            RegPassFirst.error = getString(R.string.password_mismatch)
            valid = false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(RegEmailText.text.toString()).matches()){
            RegEmail.error = getString(R.string.invalid_email)
            valid = false
        }
        if (LocalDate.parse(RegDateText.text.toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy")) >= LocalDate.now()){
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


