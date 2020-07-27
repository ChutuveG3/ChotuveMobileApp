package com.example.chotuvemobileapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.chotuvemobileapp.helpers.Utilities.EMAIL
import kotlinx.android.synthetic.main.activity_password_config.*

class PasswordConfigActivity : AppCompatActivity() {
    private val email by lazy {
        intent.getStringExtra(EMAIL)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO: * Recovery code format validation (6 characters).
        //      * Pass and confirm pass validation.

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_config)

        PassConfigProgressBar.visibility = View.GONE
        PassConfigDescriptionText.setText("We sent a new mail to $email, check and insert the code bellow:")

        ConfigPassConfirmBtn.setOnClickListener {
            // TODO: PUT sessions/password_configuration body: { recovery_code: ..., password: .. }
        }
    }
}