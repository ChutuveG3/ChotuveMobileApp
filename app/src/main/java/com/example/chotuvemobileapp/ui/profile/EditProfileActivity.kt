package com.example.chotuvemobileapp.ui.profile

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.data.users.ProfileInfoDataSource
import com.example.chotuvemobileapp.data.users.UserForModification
import com.example.chotuvemobileapp.helpers.Utilities.createDatePicker
import com.example.chotuvemobileapp.helpers.Utilities.watchText
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val prefs = applicationContext.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)

        FirstNameEditText.watchText(SaveProfileButton, this::isDataValid)
        LastNameEditText.watchText(SaveProfileButton, this::isDataValid)
        EmailEditText.watchText(SaveProfileButton, this::isDataValid)
        DOBEditText.watchText(SaveProfileButton, this::isDataValid)

        val username = intent.getStringExtra("username")!!
        val firstName = intent.getStringExtra("firstName")!!
        val lastName = intent.getStringExtra("lastName")!!
        val dob = intent.getStringExtra("dateOfBirth")!!
        val email = intent.getStringExtra("email")!!
        FirstNameEditText.setText(firstName)
        LastNameEditText.setText(lastName)
        DOBEditText.setText(dob)
        EmailEditText.setText(email)
        UsernameTextView.text = username

        createDatePicker(DOBEditText, this)

        SaveProfileButton.setOnClickListener {
            if (isEmailValid()) {
                val userInfo = UserForModification(firstName, lastName, email, dob)
                ProfileInfoDataSource.modifyProfileInfo(prefs, username, userInfo){
                    when(it){
                        "Success" -> finish()
                        "EmailInvalid" -> EmailEditText.error = getString(R.string.email_taken)
                        "Failure" -> Toast.makeText(applicationContext, getString(R.string.request_failure), Toast.LENGTH_LONG).show()
                        else -> Toast.makeText(applicationContext, getString(R.string.internal_error), Toast.LENGTH_LONG).show()
                    }
                }

            }
        }

    }

    private fun isEmailValid(): Boolean{
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(EmailEditText.text.toString()).matches() ||
            EmailEditText.length() < 5) {
            EditProfileEmail.error = getString(R.string.invalid_email)
            return false
        }
        return true
    }

    private fun isDataValid(): Boolean{
        var valid = true

        if (FirstNameEditText.text!!.isBlank()) valid = false
        if (LastNameEditText.text!!.isBlank()) valid = false
        if (EmailEditText.text!!.isBlank()) valid = false
        if(DOBEditText.text!!.isBlank()) valid = false

        return valid
    }
}
