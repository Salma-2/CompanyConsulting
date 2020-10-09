package com.example.consulting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    lateinit var authListener: FirebaseAuth.AuthStateListener
    private val TAG = "SettingsActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = Firebase.auth
        setupFirebaseAuth()

        changePasswordTv.setOnClickListener { sendResetPasswordLink() }

        saveBtn.setOnClickListener { changeEmail() }

    }

    private fun sendResetPasswordLink() {
        val user = auth.currentUser
        val email = user!!.email

        auth.sendPasswordResetEmail(email!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Password reset email sent", Toast.LENGTH_LONG).show()
                auth.signOut()

            } else {
                Log.e(TAG, "No user associated with that email")
                Toast.makeText(this, "No user associated with that email", Toast.LENGTH_LONG)
                    .show()
            }

        }

    }

    private fun changeEmail() {
        val newEmail = userEmail.text.toString()
        val password = userPassword.text.toString()
        val currentEmail = auth.currentUser!!.email

        if (!isEmpty(newEmail) && !(isEmpty(password))) {
            //different emails
            if (!newEmail.equals(currentEmail)) {
                if (newEmail.contains("@")) {
                    if (isValidDomain(newEmail)) {
                        updateUserEmail(currentEmail!!, password, newEmail)
                    } else {
                        Toast.makeText(this, "Invalid Domain", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Invalid email syntax", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(this, "No changes were made", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "You must fill out all the fields!", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUserEmail(currentEmail: String, password: String, newEmail: String) {
        val credential = EmailAuthProvider.getCredential(currentEmail, password)
        val user = auth.currentUser
        showProgressBar(progressBar)
        user!!.reauthenticate(credential)?.addOnCompleteListener { task ->
            dismissProgressBar(progressBar)
            if (task.isSuccessful) {
                auth.fetchSignInMethodsForEmail(newEmail).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val num = task.result?.signInMethods?.size
                        Log.d(TAG + " Signin methods: ", num.toString())
                        if (num == 1) {
                            Toast.makeText(this, "That email is already in use", Toast.LENGTH_LONG)
                                .show()
                        } else {
                            user.updateEmail(newEmail).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Updated email", Toast.LENGTH_LONG).show()
                                    sendVerificationEmail()
                                    auth.signOut()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "unable to update email",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }
                            }
                        }

                    } else {
                        Toast.makeText(this, "unable to update email", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Log.e(TAG, "reauthenticate not successful", task.exception)
            }
        }
    }


    private fun setupFirebaseAuth() {
        authListener = FirebaseAuth.AuthStateListener {
            val user = auth.currentUser
            if (user == null) {
                navigateTo(this, SettingsActivity(), LoginActivity::class.java, true)
                Log.d(TAG, "AuthListener: signed out - null user")
            } else {
                Log.d(TAG, "AuthListener: signed in - user with id: ${user.uid}")
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

    private fun sendVerificationEmail() {
        val user = auth.currentUser
        user!!.sendEmailVerification()?.addOnCompleteListener {
            Toast.makeText(this, "Sent verification email", Toast.LENGTH_LONG).show()
        }?.addOnFailureListener {
            Toast.makeText(this, "Couldn't send verification email", Toast.LENGTH_LONG).show()
        }
    }
}