package com.example.chotuvemobileapp

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_revert_password.*
import kotlinx.android.synthetic.main.activity_revert_password.LoginScreen


class RevertPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_revert_password)

        SendMeInstructionsBtn.isEnabled = false
        SendMeInstructionsBtn.alpha = .5f
        RevertPassProgressBar.visibility = View.GONE

        SendMeInstructionsBtn.setOnClickListener {
            // TODO: * Validar email.
            //       * get email and POST /sessions/password_recovery con body { email: .... }
            //       * On success GOTO: Pantalla para ingresar el token (6 caracteres) + pass y confirm pass.
        }
    }

    private fun showLoadingScreen() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager
                .LayoutParams.FLAG_NOT_TOUCHABLE
        )
        RevertPassProgressBar.visibility = View.VISIBLE
    }

    private fun quitLoadingScreen() {
        LoginScreen.alpha = 1F
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        RevertPassProgressBar.visibility = View.GONE
    }
}