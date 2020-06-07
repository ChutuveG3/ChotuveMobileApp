package com.example.chotuvemobileapp.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.data.users.ProfileInfoDataSource
import com.example.chotuvemobileapp.data.users.UserForModification
import com.example.chotuvemobileapp.helpers.PickRequest
import com.example.chotuvemobileapp.helpers.Utilities.createDatePicker
import com.example.chotuvemobileapp.helpers.Utilities.startSelectActivity
import com.example.chotuvemobileapp.helpers.Utilities.watchText
import com.example.chotuvemobileapp.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {
    private val prefs by lazy {
        applicationContext.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
    }
    private val userInfo by lazy {
        UserForModification(
            FirstNameEditText.text.toString(),
            LastNameEditText.text.toString(),
            EmailEditText.text.toString(),
            DOBEditText.text.toString()
        )
    }
    private val viewModel by lazy {
        ProfileViewModel.getInstance(prefs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        FirstNameEditText.watchText(SaveProfileButton, this::isDataValid)
        LastNameEditText.watchText(SaveProfileButton, this::isDataValid)
        EmailEditText.watchText(SaveProfileButton, this::isDataValid)
        DOBEditText.watchText(SaveProfileButton, this::isDataValid)

        FirstNameEditText.setText(viewModel.getUserInfo().value!!.first_name)
        LastNameEditText.setText(viewModel.getUserInfo().value!!.last_name)
        DOBEditText.setText(viewModel.getUserInfo().value!!.birthdate)
        EmailEditText.setText(viewModel.getUserInfo().value!!.email)

        createDatePicker(DOBEditText, this)

        EditProfileProgressBar.visibility = View.GONE

        ProfilePic.setOnClickListener {
            startSelectActivity(this, "image/*", "Select Pic", PickRequest.ProfilePic)
        }

        SaveProfileButton.setOnClickListener {
            if (isEmailValid()) {
                startLoadingScreen()
                ProfileInfoDataSource.modifyProfileInfo(prefs, userInfo){
                    when(it){
                        "Success" -> {
                            viewModel.updateUserInfo(userInfo.first_name, userInfo.last_name, userInfo.birthdate, userInfo.email)
                            finish()
                        }
                        "EmailInvalid" -> EditProfileEmail.error = getString(R.string.email_taken)
                        "Failure" -> Toast.makeText(applicationContext, getString(R.string.request_failure), Toast.LENGTH_LONG).show()
                        else -> Toast.makeText(applicationContext, getString(R.string.internal_error), Toast.LENGTH_LONG).show()
                    }
                    stopLoadingScreen()
                }
            }
        }
    }

    private fun stopLoadingScreen() {
        EditProfileAppbar.alpha = 1F
        EditProfileScreen.alpha = 1F
        EditProfileProgressBar.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun startLoadingScreen() {
        EditProfileProgressBar.visibility = View.VISIBLE
        EditProfileAppbar.alpha = .2F
        EditProfileScreen.alpha = .2F
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PickRequest.ProfilePic.value){
            if (resultCode == Activity.RESULT_OK){
                val uri = data!!.data
                Glide.with(applicationContext).load(uri).centerCrop().into(ProfilePic)
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
