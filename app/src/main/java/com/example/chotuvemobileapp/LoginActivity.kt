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
import com.example.chotuvemobileapp.data.users.User
import com.example.chotuvemobileapp.helpers.ThirdPartyLoginResult
import com.example.chotuvemobileapp.helpers.Utilities.BIRTH_DATE
import com.example.chotuvemobileapp.helpers.Utilities.FAILURE_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.FIREBASE_AUTH_TOKEN
import com.example.chotuvemobileapp.helpers.Utilities.INVALID_PARAMS_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.REQUEST_SIGNUP
import com.example.chotuvemobileapp.helpers.Utilities.SUCCESS_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.THIRD_PARTY_LOGIN
import com.example.chotuvemobileapp.helpers.Utilities.USERNAME
import com.example.chotuvemobileapp.helpers.Utilities.USER_NOT_REGISTERED
import com.example.chotuvemobileapp.helpers.Utilities.watchText
import com.example.chotuvemobileapp.ui.profile.FullSizeImageActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*


@Suppress("UNUSED_PARAMETER")
class LoginActivity : AppCompatActivity() {

    private val gso by lazy{
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
    }
    private val googleSignInClient by lazy {GoogleSignIn.getClient(this, gso)}
    private val fbCallbackManager by lazy { CallbackManager.Factory.create() }

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var firebaseToken: String

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

        ForgotPassText.setOnClickListener {
            startActivity(Intent(applicationContext, RevertPasswordActivity::class.java))
        }

        FacebookSignInButton.setPermissions("email", "public_profile")
        FacebookSignInButton.registerCallback(fbCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) = firebaseAuthWithFacebook(result!!.accessToken)

            override fun onCancel() =
                Toast.makeText(applicationContext, R.string.internal_error, Toast.LENGTH_LONG).show()

            override fun onError(error: FacebookException?) =
                Toast.makeText(applicationContext, R.string.internal_error, Toast.LENGTH_LONG).show()
        })
        UsernameInput.clearFocus()
        LogInUsername.clearFocus()
    }

    fun goToSignUp(view: View) = startActivity(Intent(this, SignUpActivity::class.java))

    fun signIn(view: View) {
        if (isDataValid()) {
            showLoadingScreen()
            tokenLogin(LogInUsername.text.toString(), LoginPassword.text.toString())
        }
    }

    private fun tokenLogin(user: String, pass: String) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener(this) { instanceIdResult ->
                val newToken = instanceIdResult.token
                Log.d(FIREBASE_TAG, newToken)
                LoginDataSource.tokenLogin(user, pass, newToken) {
                    when (it) {
                        FAILURE_MESSAGE -> Toast.makeText(applicationContext, getString(R.string.internal_error), Toast.LENGTH_LONG).show()
                        INVALID_PARAMS_MESSAGE -> showInvalidUsername()
                        else -> saveDataAndStartHome(it)
                    }
                    quitLoadingScreen()
                }
            }.addOnFailureListener { Exception ->
                Log.d(FIREBASE_TAG, Exception.toString())
                Toast.makeText(applicationContext, R.string.internal_error, Toast.LENGTH_LONG).show()
            }
    }

    private fun loginThirdParty(firebaseToken: String){
        showLoadingScreen()
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener(this) { instanceIdResult ->
                val newToken = instanceIdResult.token
                Log.d(FIREBASE_TAG, newToken)
                LoginDataSource.loginWithThirdParty(firebaseToken, newToken) {
                    when (it.message) {
                        FAILURE_MESSAGE, INVALID_PARAMS_MESSAGE -> Toast.makeText(applicationContext, getString(R.string.internal_error), Toast.LENGTH_LONG).show()
                        USER_NOT_REGISTERED -> startActivityForResult(Intent(this, ThirdPartyLoginActivity::class.java), REQUEST_SIGNUP)
                        else -> saveDataAndStartHome(it)
                    }
                    quitLoadingScreen()
                }
            }.addOnFailureListener { Exception ->
                Log.d(FIREBASE_TAG, Exception.toString())
                Toast.makeText(applicationContext, R.string.internal_error, Toast.LENGTH_LONG).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when(requestCode) {
                RC_SIGN_IN -> {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    try {
                        firebaseAuthWithGoogle(task.getResult(ApiException::class.java)!!.idToken!!)
                    } catch (e: ApiException) {
                        Toast.makeText(this, "Fail", Toast.LENGTH_LONG).show()
                    }
                }
                REQUEST_SIGNUP ->{
                    val names = firebaseAuth.currentUser!!.displayName!!.split(' ')
                    val lastName = names.subList(1, names.lastIndex).joinToString(" " )
                    val user = User(
                        names[0],
                        lastName,
                        firebaseAuth.currentUser!!.email!!,
                        null,
                        firebaseToken,
                        data!!.getStringExtra(USERNAME)!!,
                        data.getStringExtra(BIRTH_DATE)!!
                    )
                    LoginDataSource.addUser(user){
                        when(it){
                            SUCCESS_MESSAGE-> loginThirdParty(firebaseToken)
                            "user_name_already_exists" -> {
                                Toast.makeText(applicationContext, getString(R.string.user_taken), Toast.LENGTH_LONG).show()
                                quitLoadingScreen()
                            }
                            "user_email_already_exists" -> {
                                Toast.makeText(applicationContext, getString(R.string.email_taken), Toast.LENGTH_LONG).show()
                                quitLoadingScreen()
                            }
                            FAILURE_MESSAGE -> {
                                quitLoadingScreen()
                                Toast.makeText(applicationContext, getString(R.string.request_failure), Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                Toast.makeText( applicationContext, getString(R.string.internal_error), Toast.LENGTH_LONG).show()
                                quitLoadingScreen()
                            }
                        }
                    }
                }
                else -> fbCallbackManager.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun firebaseAuthWithFacebook(token: AccessToken){
        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { res ->
                if (res.isSuccessful){
                    firebaseAuth.currentUser!!.getIdToken(true).addOnCompleteListener {
                        if (it.isSuccessful) {
                            firebaseToken = it.result!!.token!!
                            loginThirdParty(firebaseToken)
                        }
                        else Toast.makeText(this, getString(R.string.internal_error), Toast.LENGTH_LONG).show()
                    }
                }
                else Toast.makeText(this, getString(R.string.internal_error), Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, getString(R.string.internal_error), Toast.LENGTH_LONG).show()
            }
            .addOnCanceledListener {
                Toast.makeText(this, "Canceled", Toast.LENGTH_LONG).show()
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String){
        firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
            .addOnCompleteListener {res ->
                if (res.isSuccessful){
                    firebaseAuth.currentUser!!.getIdToken(true).addOnCompleteListener {
                        if (it.isSuccessful) {
                            firebaseToken = it.result!!.token!!
                            loginThirdParty(firebaseToken)
                        }
                        else Toast.makeText(this, getString(R.string.internal_error), Toast.LENGTH_LONG).show()
                    }
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
        preferences.putBoolean(THIRD_PARTY_LOGIN, false)
        preferences.apply()
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun saveDataAndStartHome(result: ThirdPartyLoginResult) {
        val preferences = applicationContext.getSharedPreferences(
            getString(R.string.shared_preferences_file),
            Context.MODE_PRIVATE
        ).edit()
        preferences.putString("token", result.token)
        preferences.putString(USERNAME, result.username )
        preferences.putString(FIREBASE_AUTH_TOKEN, firebaseToken)
        preferences.putBoolean(THIRD_PARTY_LOGIN, true)
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
