package com.example.chotuvemobileapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chotuvemobileapp.helpers.Utilities.BIRTH_DATE
import com.example.chotuvemobileapp.helpers.Utilities.USERNAME
import com.example.chotuvemobileapp.helpers.Utilities.createDatePicker
import com.example.chotuvemobileapp.helpers.Utilities.watchText
import kotlinx.android.synthetic.main.activity_third_party_login.*

class ThirdPartyLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third_party_login)

        createDatePicker(SignupDateText, this)

        SignupUsernameText.watchText(SignUpTPButton, this::isUsernameCorrect)

        SignUpTPButton.isEnabled = false
        SignUpTPButton.alpha = 0.5F
        SignUpTPButton.setOnClickListener {
            val dataToReturn = Intent()
            dataToReturn.putExtra(USERNAME, SignupUsernameText.text.toString())
            dataToReturn.putExtra(BIRTH_DATE, SignupDateText.text.toString())
            setResult(Activity.RESULT_OK, dataToReturn)
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun isUsernameCorrect(): Boolean {
        return SignupUsernameText.text.toString().isNotBlank()
    }
}