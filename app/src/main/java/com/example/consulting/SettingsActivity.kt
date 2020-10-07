package com.example.consulting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    private val TAG = "SettingsActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        auth = Firebase.auth

        changePasswordTv.setOnClickListener {
            sendResetPasswordLink()
        }

        saveBtn.setOnClickListener { changeEmail() }

    }

    private fun sendResetPasswordLink() {
        val user = auth.currentUser
        val email = user?.email
        if (email != null) {
            auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent", Toast.LENGTH_LONG).show()
                    signOut()

                } else {
                    Log.e(TAG, "No user associated with that email")
                    Toast.makeText(this, "No user associated with that email", Toast.LENGTH_LONG)
                        .show()
                }

            }
        }
    }

    private fun changeEmail() {
        val newEmail = userEmail.text.toString()
        val password = userPassword.text.toString()
        val currentEmail = auth.currentUser?.email

        if (!isEmpty(newEmail) && !(isEmpty(password))) {
            //different emails
            if (!newEmail.equals(currentEmail)) {
                if (isValidDomain(newEmail)) {
                    updateUserEmail(currentEmail!!, password, newEmail)
                } else {
                    Toast.makeText(this, "Invalid Domain", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(this, "No Changes were Made", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "You must fill out all the fields!", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUserEmail(currentEmail: String, password: String, newEmail: String) {
        val credential = EmailAuthProvider.getCredential(currentEmail, password)
        val user = auth.currentUser
        user?.reauthenticate(credential)?.addOnCompleteListener { task ->
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
                                if (task.isSuccessful){
                                    Toast.makeText(this, "Updated email", Toast.LENGTH_LONG).show()
                                    sendVerificationEmail(newEmail)
                                    signOut()
                                }
                                else{
                                Toast.makeText(this, "unable to update email", Toast.LENGTH_LONG)
                                    .show()
                            }
                            }
                        }

                    } else {
                        Toast.makeText(this, "unable to update email", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Log.e(TAG, "Task not successful", task.exception)
            }
        }?.addOnFailureListener { Log.e(TAG, "failure") }
    }

    private fun signOut() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun sendVerificationEmail(email: String) {
        val user = auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener {
            Toast.makeText(this, "Sent verification email", Toast.LENGTH_LONG).show()
        }?.addOnFailureListener {
            Toast.makeText(this, "Couldn't send verification email", Toast.LENGTH_LONG).show()
        }
    }
}