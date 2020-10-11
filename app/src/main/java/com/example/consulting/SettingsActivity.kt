package com.example.consulting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.consulting.models.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    lateinit var authListener: FirebaseAuth.AuthStateListener
    lateinit var database: FirebaseDatabase
    lateinit var ref: DatabaseReference
    private val TAG = "SettingsActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = Firebase.auth
        database = Firebase.database
        setupFirebaseAuth()
        getUserDetails()

        changePasswordTv.setOnClickListener { sendResetPasswordLink() }

        saveBtn.setOnClickListener {
            showProgressBar(progressBar)
            insertUserDetails(auth.currentUser)
            changeEmail()
            dismissProgressBar(progressBar)
        }

    }

    private fun isEmailChanged(): Boolean {
        val newEmail = userEmail.text.toString()
        val currentEmail = auth.currentUser!!.email

        if (!isEmpty(newEmail) && !newEmail.equals(currentEmail)) return true
        return false

    }

    private fun changeEmail() {
        val newEmail = userEmail.text.toString()
        val password = userPassword.text.toString()
        val currentEmail = auth.currentUser!!.email
        if (isEmailChanged()) {
            if (!isEmpty(password)) {
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
                Toast.makeText(
                    this,
                    "You must confirm your password to change email!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    private fun updateUserEmail(currentEmail: String, password: String, newEmail: String) {
        val credential = EmailAuthProvider.getCredential(currentEmail, password)
        val user = auth.currentUser
        user!!.reauthenticate(credential)?.addOnCompleteListener { task ->
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
                                    Toast.makeText(this, "email updated", Toast.LENGTH_LONG).show()
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





    private fun getUserDetails() {
        userEmail.setText(auth.currentUser!!.email)


        ref= database.reference.child(getString(R.string.dbnode_users))
        /*-----------Query1-------------*/
        val query1 = ref.orderByKey().equalTo(auth.currentUser!!.uid)
        query1.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(singleSnapshot in snapshot.children){
                    val user = singleSnapshot.getValue(User::class.java)
                    Log.d(TAG , "onDataChange: Query(orderByKey) found user: " + user.toString())
                    userName.setText(user!!.name)
                    userPhoneNumber.setText(user!!.phone)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        /*-----------Query2-------------*/
//        val query2 = ref.orderByChild(getString(R.string.field_uid)).equalTo(auth.currentUser!!.uid)
//        query2.addListenerForSingleValueEvent(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//               for(singleSnapshot in snapshot.children){
//                   val user = singleSnapshot.getValue(User::class.java)
//                   Log.d(TAG , "onDataChange: Query(orderByKey) found user: " + user.toString())
//                   userName.setText(user!!.name)
//                   userPhoneNumber.setText(user!!.phone)
//               }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//
//        })



    }

    private fun insertUserDetails(user: FirebaseUser?) {
        ref = database.reference.child(getString(R.string.dbnode_users)).child(user!!.uid)

        /*--------------- set name ----------------*/
        ref.child(getString(R.string.field_name)).setValue(userName.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, " name inserted")
                } else {
                    Log.d(TAG, "Can not insert name ", task.exception)
                }
            }

        /*--------------- set phone ----------------*/
        ref.child(getString(R.string.field_phone)).setValue(userPhoneNumber.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, " phone# inserted")
                } else {
                    Log.d(TAG, "Can not insert phone# ", task.exception)
                }
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
}

