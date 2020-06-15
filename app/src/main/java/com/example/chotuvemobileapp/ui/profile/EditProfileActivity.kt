package com.example.chotuvemobileapp.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.example.chotuvemobileapp.helpers.Utilities
import com.example.chotuvemobileapp.helpers.Utilities.createDatePicker
import com.example.chotuvemobileapp.helpers.Utilities.getFileName
import com.example.chotuvemobileapp.helpers.Utilities.startSelectActivity
import com.example.chotuvemobileapp.helpers.Utilities.watchText
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {

    private var uri: Uri? = null
    private lateinit var picUrl : String
    private var fileName: String? = null
    private val prefs by lazy {
        applicationContext.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
    }
    private val mStorageRef by lazy {
        FirebaseStorage.getInstance().reference
    }
    private val userInfo by lazy {
        UserForModification(
            FirstNameEditText.text.toString(),
            LastNameEditText.text.toString(),
            EmailEditText.text.toString(),
            DOBEditText.text.toString(),
            picUrl
        )
    }
    private val firstName by lazy{
        intent.getStringExtra(Utilities.FIRST_NAME)
    }
    private val lastName by lazy{
        intent.getStringExtra(Utilities.LAST_NAME)
    }
    private val email by lazy{
        intent.getStringExtra(Utilities.EMAIL)
    }
    private val birthDate by lazy{
        intent.getStringExtra(Utilities.BIRTH_DATE)
    }
    private val profilePicUrl by lazy{
        intent.getStringExtra(Utilities.PIC_URL)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        FirstNameEditText.watchText(SaveProfileButton, this::isDataValid)
        LastNameEditText.watchText(SaveProfileButton, this::isDataValid)
        EmailEditText.watchText(SaveProfileButton, this::isDataValid)
        DOBEditText.watchText(SaveProfileButton, this::isDataValid)

        FirstNameEditText.setText(firstName)
        LastNameEditText.setText(lastName)
        DOBEditText.setText(birthDate)
        EmailEditText.setText(email)
        if (profilePicUrl != null){
            Glide.with(this).load(Uri.parse(profilePicUrl)).centerCrop().into(ProfilePic)
        }

        createDatePicker(DOBEditText, this)

        EditProfileProgressBar.visibility = View.GONE

        ProfilePic.setOnClickListener {
            startSelectActivity(this, "image/*", "Select Pic", PickRequest.ProfilePic)
        }

        SaveProfileButton.setOnClickListener {
            if (isEmailValid()) {
                startLoadingScreen()
                try {
                    val storageReference: StorageReference = mStorageRef.child(fileName!!)
                    storageReference.putFile(uri!!).addOnSuccessListener {
                        storageReference.downloadUrl.addOnSuccessListener { url ->
                            picUrl = url.toString()
                            ProfileInfoDataSource.modifyProfileInfo(prefs, userInfo) {
                                when (it) {
                                    "Success" -> {
                                        val dataToReturn = Intent()
                                        dataToReturn.putExtra(Utilities.FIRST_NAME, userInfo.first_name)
                                        dataToReturn.putExtra(Utilities.LAST_NAME, userInfo.last_name)
                                        dataToReturn.putExtra(Utilities.BIRTH_DATE, userInfo.birthdate)
                                        dataToReturn.putExtra(Utilities.EMAIL, userInfo.email)
                                        dataToReturn.putExtra(Utilities.PIC_URL, userInfo.profile_img_url)
                                        setResult(Activity.RESULT_OK, dataToReturn)
                                        finish()
                                    }
                                    "EmailInvalid" -> EditProfileEmail.error = getString(R.string.email_taken)
                                    "Failure" -> Toast.makeText(applicationContext,
                                        getString(R.string.request_failure),
                                        Toast.LENGTH_LONG
                                    ).show()
                                    else -> Toast.makeText(applicationContext,
                                        getString(R.string.internal_error),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                stopLoadingScreen()
                            }
                        }.addOnFailureListener {
                            fail()
                        }
                    }.addOnFailureListener {
                        fail()
                    }
                }
                catch (e: Exception){
                    fail()
                }
            }
        }
    }

    private fun fail(){
        stopLoadingScreen()
        Toast.makeText(
            applicationContext,
            getString(R.string.internal_error),
            Toast.LENGTH_LONG
        ).show()
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
                uri = data!!.data
                fileName = getFileName(uri!!, contentResolver)
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
