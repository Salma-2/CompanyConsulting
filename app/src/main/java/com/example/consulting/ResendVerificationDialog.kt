package com.example.consulting

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.resend_verification_dialog.*
import kotlinx.android.synthetic.main.resend_verification_dialog.view.*


class ResendVerificationDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.resend_verification_dialog, container, false)

        view.closeDialog.setOnClickListener {
            dismiss()
        }

        view.confirmDialog.setOnClickListener {
            Log.d("ResendVerification", "ConfirmDialog")

        }
        return view
    }




}