package com.example.consulting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.consulting.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference
    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth
        database = Firebase.database

        registerBtn.setOnClickListener {
            doRegisterWork()
        }

    }


    private fun doRegisterWork() {
        val email = userEmail.text.toString()
        val password = userPassword.text.toString()
        val confirmPassword = userPasswordConfirm.text.toString()
        if ((!isEmpty(email)) &&
            (!isEmpty(password)) &&
            (!isEmpty(confirmPassword))
        ) {
            if (email.contains("@")) {
                if (isValidDomain(email)) {
                    if (password == confirmPassword) {
                        showProgressBar(progressBar)
                        registerNewEmail(email, password)
                    } else {
                        Toast.makeText(this, "Passwords do not Match", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Please Register With Company Email", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(this, "Invalid email syntax", Toast.LENGTH_LONG).show()
            }

        } else {
            Toast.makeText(this, "You must fill out all the fields!", Toast.LENGTH_LONG).show()
        }
    }

    private fun registerNewEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            dismissProgressBar(progressBar)
            if (task.isSuccessful) {
                Log.d(
                    TAG,
                    "create User With Email:success, id:" + (auth.currentUser!!.uid)
                )
                val user = auth.currentUser
                insertUserData(user)
                sendVerificationEmail()
            } else {
                Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "createUserWithEmail:failure", task.exception)
                navigateTo(this, RegisterActivity(), LoginActivity::class.java, true)
            }
        }

    }


    private fun insertUserData(user: FirebaseUser?) {
        val insertedUser = User()
        insertedUser.user_id = user!!.uid
        val index = user.email!!.indexOf("@")
        insertedUser.name = user.email!!.substring(0, index)

        ref = database.reference.child(getString(R.string.dbnode_users)).child(user.uid)
        ref.setValue(insertedUser).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "OnSuccess RegisterActivity: User Inserted ")
                auth.signOut()
                navigateTo(this, RegisterActivity(), LoginActivity::class.java, true)
            } else {
                Log.d(TAG, "OnFailure RegisterActivity: Can not insert user: ", task.exception)
                auth.signOut()
                navigateTo(this, RegisterActivity(), LoginActivity::class.java, true)
            }
        }


    }


    private fun sendVerificationEmail() {
        val user = auth.currentUser
        user!!.sendEmailVerification().addOnCompleteListener {
            Toast.makeText(this, "Verification email sent", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Couldn't send verification email", Toast.LENGTH_LONG).show()
        }
    }

}