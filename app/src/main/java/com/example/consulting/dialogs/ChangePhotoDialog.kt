package com.example.consulting.dialogs

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.consulting.CAMERA_REQUEST_CODE
import com.example.consulting.OnPhotoRecievedListener
import com.example.consulting.PICKFILE_REQUEST_CODE
import com.example.consulting.R
import kotlinx.android.synthetic.main.dialog_change_photo.view.*


class ChangePhotoDialog(val onPhotoRecievedListener: OnPhotoRecievedListener) : DialogFragment() {
    private val TAG = "ChangePhotoDialog"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.dialog_change_photo, container, false)

        view.dialogChoosePhoto.setOnClickListener {
            Log.d(TAG, "onClick: accessing phones memory.")
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICKFILE_REQUEST_CODE)
        }

        view.dialogOpenCamera.setOnClickListener {
            Log.d(TAG, "onClick: starting camera.")
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                val bitmap = data!!.extras!!.get("data") as Bitmap
                onPhotoRecievedListener.getImageBitmap(bitmap)
                dismiss()

            } else if (requestCode == PICKFILE_REQUEST_CODE) {
                val selectedImageUri = data!!.data
                Log.d(TAG, "onActivityResult: image: " + selectedImageUri)

                onPhotoRecievedListener.getImagePath(selectedImageUri!!)
                dismiss()
            }
        }
    }

}