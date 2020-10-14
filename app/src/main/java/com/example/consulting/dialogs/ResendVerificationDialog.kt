package com.example.consulting.dialogs


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.consulting.R
import com.example.consulting.dismissProgressBar
import com.example.consulting.isEmpty
import com.example.consulting.showProgressBar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_resend_verification.*
import kotlinx.android.synthetic.main.dialog_resend_verification.view.*


class ResendVerificationDialog(val mContext: Context) : DialogFragment() {

    lateinit var auth: FirebaseAuth
    private val TAG = "ResendVerification"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        val view = inflater.inflate(R.layout.dialog_resend_verification, container, false)

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
                Toast.makeText(mContext, "You must fill out all the fields!", Toast.LENGTH_LONG).show()
            }

        }

        return view
    }


    private fun authenticateAndResendEmail(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        showProgressBar(progressBar)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            dismissProgressBar(progressBar)
            if (task.isSuccessful) {
                Log.d(TAG, "re-authenticate success")
                sendVerificationEmail()
                auth.signOut()
                dismiss()
            }
        }.addOnFailureListener {
            Toast.makeText(
                mContext,
                "Invalid Credentials" + "\n" + "Reset Your Password and Try Again",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun sendVerificationEmail() {
        val user = auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener {task->
            if(task.isSuccessful){
                Toast.makeText(mContext, "Sent", Toast.LENGTH_LONG).show()
                Log.d(TAG, "Sent verification email")
            }
            else{
                Log.e(TAG, "Couldn't send verification email ", task.exception)
                Toast.makeText(mContext, "Couldn't send verification email", Toast.LENGTH_LONG).show()
            }
        }
    }



}