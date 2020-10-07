package com.example.consulting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        resendVerMailTv.setOnClickListener {
            val dialog = ResendVerificationDialog()
            dialog.show(supportFragmentManager , "Resend Dialog")
        }
    }

    private fun doLoginWork() {
        val email = userEmail.text.toString()
        val password = userPassword.text.toString()

        if ((!isEmpty(email)) &&
            (!isEmpty(password))
        ) {


            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
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
                if (user.isEmailVerified)
                    {
                        Log.d(TAG, "AuthListener: signed in")}
                else {
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
        if (authListener != null) {
            auth.removeAuthStateListener(authListener)
        }
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        if (authListener != null) {
            auth.addAuthStateListener(authListener)
        }
    }
}