package com.example.consulting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_settings.*
import kotlin.math.sign

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



    private fun signOut() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}