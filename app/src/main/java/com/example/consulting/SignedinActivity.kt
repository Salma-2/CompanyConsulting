package com.example.consulting

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.consulting.utility.UniversalImageLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.nostra13.universalimageloader.core.ImageLoader

class SignedinActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var authListener: FirebaseAuth.AuthStateListener
    private val TAG = "SignedinActivity"
    override fun onCreate(savedInstanceState: Bundle?) {

        auth = Firebase.auth
        setupFirebaseAuth()
        initImageLoader()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signedin)

        setUserDetails()
        getUserDetails()
    }

    private fun initImageLoader(){
        val imageLoader = UniversalImageLoader(this)
        ImageLoader.getInstance().init(imageLoader.getConfig())

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.signedin_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_signout -> {
                auth.signOut()
                navigateTo(this, SignedinActivity(), LoginActivity::class.java, true)
                return true
            }
            R.id.action_settings -> {
                navigateTo(this, SignedinActivity(), SettingsActivity::class.java)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        checkAuthenticationState()
    }

    private fun checkAuthenticationState() {
        //go back to login screen
        if (auth.currentUser == null) {
            Log.d(TAG, "user is null")
            navigateTo(this, SignedinActivity(), LoginActivity::class.java, true)
        } else {
            Log.d(TAG, "user is not null")
        }
    }


    private fun getUserDetails() {
        val user = auth.currentUser
        user?.let {
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl
            val uId = user.uid
            val emailVerified = user.isEmailVerified

            val details = "Name: $name,\n" +
                    " Email: $email,\n" +
                    "PhotoUrl: $photoUrl,\n" +
                    "Id: $uId,\n" +
                    "isVerified? $emailVerified."
            Log.d(TAG, details)

        }
    }

    private fun setUserDetails() {
        val user = auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            displayName = "Salma"
            photoUri =
                Uri.parse("https://1.bp.blogspot.com/-47ehH6VQ8Dg/U1io7iuzMoI/AAAAAAAAAa8/rh8gAHDL12k/s1600/android.jpg")
        }

        user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful)
                Log.d(TAG, "Profile Updated")
            else {
                Log.d(TAG, "Can not update profile, " , task.exception)
            }
        }

    }

    private fun setupFirebaseAuth() {

        Log.d(TAG, "setupFirebaseAuth: started.")
        authListener = FirebaseAuth.AuthStateListener {
            val user = auth.currentUser
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.uid)
            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out")
                navigateTo(this, SignedinActivity(), LoginActivity::class.java, true)
            }
        }

    }
}