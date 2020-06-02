package com.example.chotuvemobileapp.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.data.users.ProfileInfoDataSource
import com.example.chotuvemobileapp.data.users.UserForModification
import com.example.chotuvemobileapp.helpers.PickRequest
import com.example.chotuvemobileapp.helpers.Utilities.createDatePicker
import com.example.chotuvemobileapp.helpers.Utilities.startSelectActivity
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

        FirstNameEditText.setText(intent.getStringExtra("firstName"))
        LastNameEditText.setText(intent.getStringExtra("lastName"))
        DOBEditText.setText(intent.getStringExtra("dateOfBirth"))
        EmailEditText.setText(intent.getStringExtra("email"))

        createDatePicker(DOBEditText, this)

        EditProfileProgressBar.visibility = View.GONE

        ProfilePic.setOnClickListener {
            startSelectActivity(this, "image/*", "Select Pic", PickRequest.ProfilePic)
        }

        BackgroundPic.setOnClickListener {
            startSelectActivity(this, "image/*", "Select Pic", PickRequest.BackgroundPic)
        }

        SaveProfileButton.setOnClickListener {
            if (isEmailValid()) {
                EditProfileProgressBar.visibility = View.VISIBLE
                EditProfileAppbar.alpha = .2F
                EditProfileScreen.alpha = .2F
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                val userInfo = UserForModification(FirstNameEditText.text.toString(),
                                                    LastNameEditText.text.toString(),
                                                    EmailEditText.text.toString(),
                                                    DOBEditText.text.toString())
                ProfileInfoDataSource.modifyProfileInfo(prefs, userInfo){
                    when(it){
                        "Success" -> finish()
                        "EmailInvalid" -> EditProfileEmail.error = getString(R.string.email_taken)
                        "Failure" -> Toast.makeText(applicationContext, getString(R.string.request_failure), Toast.LENGTH_LONG).show()
                        else -> Toast.makeText(applicationContext, getString(R.string.internal_error), Toast.LENGTH_LONG).show()
                    }
                    EditProfileAppbar.alpha = 1F
                    EditProfileScreen.alpha = 1F
                    EditProfileProgressBar.visibility = View.GONE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }

            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PickRequest.ProfilePic.value){
            if (resultCode == Activity.RESULT_OK){
                val uri = data!!.data
                Glide.with(applicationContext).load(uri).centerCrop().into(ProfilePic)
            }
        }
        else if (requestCode == PickRequest.BackgroundPic.value){
            if (resultCode == Activity.RESULT_OK){
                val uri = data!!.data
                Glide.with(applicationContext).load(uri).centerCrop().into(BackgroundPic)
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
