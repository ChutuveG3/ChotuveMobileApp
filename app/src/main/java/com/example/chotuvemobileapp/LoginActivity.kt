package com.example.chotuvemobileapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chotuvemobileapp.data.repositories.LoginDataSource
import com.example.chotuvemobileapp.helpers.Utilities.FAILURE_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.INVALID_PARAMS_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.USERNAME
import com.example.chotuvemobileapp.helpers.Utilities.watchText
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Exception


class LoginActivity : AppCompatActivity() {

    private val gso by lazy{
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("GoogleToken")
            .requestEmail()
            .requestProfile()
            .build()
    }
    private val googleSignInClient by lazy {GoogleSignIn.getClient(this, gso)}

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        SignInButton.isEnabled = false
        SignInButton.alpha = .5f

        LoginProgressBar.visibility = View.GONE

        LogInUsername.watchText(SignInButton, this::isDataCorrect)
        LoginPassword.watchText(SignInButton, this::isDataCorrect)

        GoogleSignInButton.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
        }
    }

    fun goToSignUp(view: View) = startActivity(Intent(this, SignUpActivity::class.java))

    fun signIn(view: View) {
        if (isDataValid()) {
            showLoadingScreen()
            FirebaseInstanceId.getInstance().instanceId
                .addOnSuccessListener(this@LoginActivity) { instanceIdResult ->
                    val newToken = instanceIdResult.token
                    Log.d(FIREBASE_TAG, newToken)

                    val username = LogInUsername.text.toString()
                    val pass = LoginPassword.text.toString()
                    LoginDataSource.tokenLogin(username, pass, newToken) {
                        when (it) {
                            FAILURE_MESSAGE -> Toast.makeText(
                                applicationContext, getString(R.string.internal_error),
                                Toast.LENGTH_LONG
                            ).show()
                            INVALID_PARAMS_MESSAGE -> showInvalidUsername()
                            else -> saveDataAndStartHome(it)
                        }
                        quitLoadingScreen()
                    }
                }.addOnFailureListener { Exception ->
                    Log.d(FIREBASE_TAG, Exception.toString())
                    Toast.makeText(applicationContext, R.string.internal_error, Toast.LENGTH_LONG)
                        .show()
                }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                firebaseAuthWithGoogle(task.getResult(ApiException::class.java)!!.idToken!!)
            }
            catch (e: ApiException) {
                Toast.makeText(this, "Fail", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String){
        firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val user = firebaseAuth.currentUser
                    //Login con el app server
                }
                else Toast.makeText(this, "Fail", Toast.LENGTH_LONG).show()
            }
    }
    private fun showLoadingScreen() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        LoginScreen.alpha = .2F
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager
                .LayoutParams.FLAG_NOT_TOUCHABLE
        )
        LoginProgressBar.visibility = View.VISIBLE
    }

    private fun quitLoadingScreen() {
        LoginScreen.alpha = 1F
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        LoginProgressBar.visibility = View.GONE
    }

    private fun saveDataAndStartHome(token: String) {
        val preferences = applicationContext.getSharedPreferences(
            getString(R.string.shared_preferences_file),
            Context.MODE_PRIVATE
        ).edit()
        preferences.putString("token", token)
        preferences.putString(USERNAME, LogInUsername.text.toString())
        preferences.putString("password", LoginPassword.text.toString())
        preferences.apply()
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun showInvalidUsername() {
        LoginScreen.alpha = 1F
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        LoginProgressBar.visibility = View.GONE
        UsernameInput.error = getString(R.string.failed_login)
        PasswordInput.error = getString(R.string.failed_login)
        PasswordInput.getChildAt(1).visibility = View.GONE
    }

    private fun isDataCorrect(): Boolean {
        UsernameInput.error = null
        PasswordInput.error = null
        return LogInUsername.text.toString().isNotEmpty() && LoginPassword.text.toString().isNotEmpty()
    }

    private fun isDataValid(): Boolean{
        var valid = true

        UsernameInput.error = null
        PasswordInput.error = null

        if (LoginPassword.text.toString().length < PASSWORD_MIN_LENGTH){
            PasswordInput.error = getString(R.string.invalid_pass)
            valid = false
        }
        if (LogInUsername.length() > USERNAME_MAX_LENGTH){
            UsernameInput.error = getString(R.string.invalid_username)
            valid = false
        }
        return valid
    }

    companion object {
        private const val USERNAME_MAX_LENGTH = 30
        private const val PASSWORD_MIN_LENGTH = 6
        private const val FIREBASE_TAG = "FIREBASE"
        private const val RC_SIGN_IN = 9001
    }
}
