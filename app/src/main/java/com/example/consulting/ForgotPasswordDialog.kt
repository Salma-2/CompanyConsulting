package com.example.consulting


import android.content.Context
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


class ForgotPasswordDialog(val mContext: Context) : DialogFragment() {

    lateinit var auth: FirebaseAuth
    private val TAG = "ForgotPassword"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        val view = inflater.inflate(R.layout.forgot_password_dialog, container, false)

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