package com.example.chotuvemobileapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.chotuvemobileapp.helpers.Utilities.EMAIL
import com.example.chotuvemobileapp.helpers.Utilities.watchText
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

        val message = "We sent an email to $email, please insert the code you received below:"

        PassConfigProgressBar.visibility = View.GONE
        PassConfigDescriptionText.text = message

        PassConfigCode.watchText(ConfigPassConfirmBtn, this::isDataValid)
        PassConfigPassword.watchText(ConfigPassConfirmBtn, this::isDataValid)
        PassConfigConfirmCPassword.watchText(ConfigPassConfirmBtn, this::isDataValid)

        ConfigPassConfirmBtn.setOnClickListener {
            // TODO: PUT sessions/password_configuration body: { recovery_code: ..., password: .. }
        }
    }

    private fun isDataValid(): Boolean =
        PassConfigCode.text.toString().length == 6 &&
                PassConfigPassword.text.toString().isNotBlank() &&
                PassConfigConfirmCPassword.text.toString().isNotBlank() &&
                PassConfigPassword.text.toString().length >= 6 &&
                PassConfigPassword.text.toString() == PassConfigConfirmCPassword.text.toString()
}