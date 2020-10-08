package com.example.consulting


import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.resend_verification_dialog.*
import kotlinx.android.synthetic.main.resend_verification_dialog.view.*


class ResendVerificationDialog : DialogFragment() {

    lateinit var auth: FirebaseAuth
    private val TAG = "ResendVerification"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        val view = inflater.inflate(R.layout.resend_verification_dialog, container, false)

        view.closeDialog.setOnClickListener {
            dismiss()
        }

        view.confirmDialog.setOnClickListener {
            val email = userEmail.text.toString()
            val password = userPassword.text.toString()

            if (!isEmpty(email) && !isEmpty(password)) {
                authenticateAndResendEmail(email, password)
            }
            else{
                Toast.makeText(context, "You must fill out all the fields!", Toast.LENGTH_LONG).show()
            }

            Log.d(TAG, "ConfirmDialog")

        }
        return view
    }


    private fun authenticateAndResendEmail(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "re-authenticate success")
                sendVerificationEmail(email)
                auth.signOut()
            }
        }.addOnFailureListener {
            Toast.makeText(
                context,
                "Invalid Credentials" + "\n" + "Reset Your Password and Try Again",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun sendVerificationEmail(email: String) {
        val user = auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener {
            Toast.makeText(context, "Sent", Toast.LENGTH_LONG).show()
            dismiss()
        }?.addOnFailureListener {
            Toast.makeText(context, "Couldn't send verification email", Toast.LENGTH_LONG).show()
        }
    }


}