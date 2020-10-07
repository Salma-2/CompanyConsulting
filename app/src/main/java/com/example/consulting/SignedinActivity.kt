package com.example.consulting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignedinActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    private val TAG = "SignedinActivity"
    override fun onCreate(savedInstanceState: Bundle?) {

        auth = Firebase.auth

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signedin)

        getUserDetails()
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
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        checkAuthenticationState()
    }

    private fun checkAuthenticationState(){
        //go back to login screen
        if(auth.currentUser == null){
            Log.d(TAG , "user is null")
            val intent =Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        else{
            Log.d(TAG , "user is not null")
        }
    }


    private fun getUserDetails(){
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
}