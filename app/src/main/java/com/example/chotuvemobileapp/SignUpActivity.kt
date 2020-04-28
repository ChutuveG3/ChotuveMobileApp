package com.example.chotuvemobileapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chotuvemobileapp.data.LoginDataSource.addUser
import com.example.chotuvemobileapp.data.LoginDataSource.userExists
import kotlinx.android.synthetic.main.activity_sign_up.*
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
                val text = "$mDay/$mMonth/$mYear"
                RegDateText.setText(text)

            }, year, month, day)
            dialog.show()
        }

        SignUpButton.setOnClickListener{
            addUser(RegUsernameText.toString(), RegNameText.toString(), RegLastNameText.toString(), RegEmailText.toString(),
                RegPwFirstText.toString(), RegDateText.toString())
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    fun validate(): Boolean {
        var valid = true

        val name = RegNameText.toString()
        val lastName = RegLastNameText.toString()
        val username = RegUsernameText.toString()
        val email = RegEmailText.toString()
        val password = RegPwFirstText.toString()
        val reEnterPassword = RegPwSecondText.toString()

        RegNameText.error = null
        RegLastNameText.error = null
        RegUsernameText.error = null
        RegEmailText.error = null
        RegPwFirstText.error = null
        RegPwSecondText.error = null

        if (name.isEmpty()){
            RegNameText.error = "Name is required"
            valid = false
        }
        if (lastName.isEmpty()){
            RegLastNameText.error = "Last Name is required"
            valid = false
        }
        if (username.isEmpty()){
            RegUsernameText.error = "Name is required"
            valid = false
        }
        if (userExists(username)){
            RegUsernameText.error = "Username already taken"
            valid = false
        }
        if (email.isEmpty()){
            RegEmailText.error = "Email is required"
            valid = false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            RegEmailText.error = "Invalid email"
            valid = false
        }
        if (password != reEnterPassword){
            RegPwFirstText.error = "Passwords don't match"
            valid = false
        }
        return valid
    }
    fun EditText.onTextChanged(onTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                SignUpButton.isEnabled = validate()
                if (!SignUpButton.isEnabled) Toast.makeText(baseContext, "Sign up Failed", Toast.LENGTH_LONG).show()
            }
        })
    }
}


