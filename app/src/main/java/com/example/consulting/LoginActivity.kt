package com.example.consulting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.consulting.dialogs.ForgotPasswordDialog
import com.example.consulting.dialogs.ResendVerificationDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.userEmail
import kotlinx.android.synthetic.main.activity_login.userPassword


class LoginActivity : AppCompatActivity() {
    private val TAG = this::class.simpleName
    private lateinit var auth: FirebaseAuth
    private lateinit var authListener: FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth

        setupFirebaseAuth()

        loginBtn.setOnClickListener {
            doLoginWork()
        }

        registerTv.setOnClickListener {
            navigateTo(this, LoginActivity(), RegisterActivity::class.java)
        }

        forgotPassTv.setOnClickListener {
            val dialog = ForgotPasswordDialog(this)
            dialog.show(supportFragmentManager, "Forgot passowrd")
        }
        resendVerMailTv.setOnClickListener {
            val dialog = ResendVerificationDialog(this)
            dialog.show(supportFragmentManager, "Resend Dialog")
        }
    }

    private fun doLoginWork() {
        val email = userEmail.text.toString()
        val password = userPassword.text.toString()

        if ((!isEmpty(email)) &&
            (!isEmpty(password))
        ) {
            showProgressBar(progressBar)
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                dismissProgressBar(progressBar)
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                } else {
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener {
                Toast.makeText(
                    this, "Authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }



        } else {
            Toast.makeText(this, "You must fill out all the fields!", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupFirebaseAuth() {
        authListener = FirebaseAuth.AuthStateListener {
            val user = auth.currentUser
            if (user != null) {
                if (user.isEmailVerified) {
                    Log.d(TAG, "AuthListener: signed in")
                    navigateTo(this, LoginActivity(), SignedinActivity::class.java, true)
                } else {
                    Toast.makeText(
                        this, "Check Your Email Inbox for a Verification Link.",
                        Toast.LENGTH_SHORT
                    ).show()
                    auth.signOut()
                }
            } else {
                Log.e(TAG, "AuthListener: signed out")
            }
        }

    }

    override fun onStop() {

            auth.removeAuthStateListener(authListener)

        super.onStop()
    }

    override fun onStart() {
        super.onStart()

            auth.addAuthStateListener(authListener)

    }

}