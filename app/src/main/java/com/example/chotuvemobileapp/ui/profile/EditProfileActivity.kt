package com.example.chotuvemobileapp.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.example.chotuvemobileapp.R
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        FirstNameEditText.setText(intent.getStringExtra("firstName"))
        LastNameEditText.setText(intent.getStringExtra("lastName"))
        DOBEditText.setText(intent.getStringExtra("dateOfBirth"))
        UsernameTextView.text = intent.getStringExtra("username")

        SaveProfileButton.setOnClickListener {
            supportFragmentManager.popBackStack("ProfileFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            finish()
        }

    }
}
