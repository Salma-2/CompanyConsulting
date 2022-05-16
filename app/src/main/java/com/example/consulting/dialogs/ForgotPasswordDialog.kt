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
import com.example.consulting.isEmpty
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_resend_verification.*
import kotlinx.android.synthetic.main.dialog_resend_verification.view.*


class ForgotPasswordDialog(val mContext: Context) : DialogFragment() {

    lateinit var auth: FirebaseAuth
    private val TAG = "ForgotPassword"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        val view = inflater.inflate(R.layout.dialog_forgot_password, container, false)

        view.closeDialog.setOnClickListener {
            dismiss()
        }

        view.confirmDialog.setOnClickListener {
            val email = userEmail.text.toString()

            if (!isEmpty(email) ) {
                sendResetPasswordLink(email)
                dismiss()
            }
            else{
                Toast.makeText(mContext, "You must fill out the field!", Toast.LENGTH_LONG).show()
            }

        }

        return view
    }

    private fun sendResetPasswordLink(email: String) {


        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(mContext, "Password reset email sent", Toast.LENGTH_LONG).show()
                auth.signOut()

            } else {
                Log.e(TAG, "No user associated with that email")
                Toast.makeText(mContext, "No user associated with that email", Toast.LENGTH_LONG)
                    .show()
            }

        }

    }



}