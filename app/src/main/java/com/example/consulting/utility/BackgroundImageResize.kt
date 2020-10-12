package com.example.consulting.utility

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.consulting.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import java.io.IOException

class BackgroundImageResize(val bm: Bitmap?, val context: Context, val auth: FirebaseAuth) :
    AsyncTask<Uri, Int, ByteArray>() {


    val storage: FirebaseStorage
    var database: FirebaseDatabase
    lateinit var storageRef: StorageReference
    lateinit var dbRef: DatabaseReference
    val TAG = "SettingsActivity"
    var bitmap: Bitmap? = null
    var mBytes: ByteArray? = null

    init {
        if (bm != null) {
            bitmap = bm
        }
        storage = Firebase.storage
        database = Firebase.database
    }

    override fun onPreExecute() {
        super.onPreExecute()
        Toast.makeText(context, "compressing image", Toast.LENGTH_SHORT).show()
    }

    override fun doInBackground(vararg params: Uri?): ByteArray? {
        if (bitmap == null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                    context.getContentResolver(),
                    params[0]
                )
                Log.d(
                    TAG,
                    "doInBackground: bitmap size: megabytes: " + bitmap!!.getByteCount() / MB + " MB"
                )
            } catch (e: IOException) {
                Log.e(TAG, "doInBackground: IOException: ", e.cause);
            }
        }
        var bytes: ByteArray? = null
        for (i in 1..11) {
            if (i == 10) {
                Toast.makeText(context, "That image is too large.", Toast.LENGTH_SHORT).show()
                break
            }
            bytes = getBytesFromBitmap(bitmap!!, 100 / i)
            Log.e(
                TAG,
                "doInBackground: megabytes: (" + (11 - i) + "0%) " + bytes!!.size / MB + " MB"
            )
            if (bytes.size / MB < MB_THRESHHOLD) {
                return bytes
            }
        }
        return bytes
    }

    override fun onPostExecute(bytes: ByteArray?) {
        super.onPostExecute(bytes)
        mBytes = bytes

        //execute the upload
        executeUploadTask()
    }

    private fun executeUploadTask() {
        var progress: Long = 0
        storageRef =
            storage.reference.child(FIREBASE_IMAGE_STORAGE + "/" + auth.currentUser!!.uid + "/profile_image")
        if (mBytes!!.size / MB < MB_THRESHHOLD) {
            var metadata = storageMetadata {
                contentType = "image/jpg"
                contentLanguage = "en"
                setCustomMetadata("Salma's special meta data", "JK nothing special here")
                setCustomMetadata("location", "Iceland")
            }
            val uploadTask = storageRef.putBytes(mBytes!!, metadata)
//            val uploadTask = storageRef.putBytes(mBytes!!)
            uploadTask.addOnSuccessListener { taskSnapshot ->
                val result = taskSnapshot.metadata?.reference?.downloadUrl
                result?.addOnSuccessListener { uri ->
                    val firebaseUrl = uri.toString()
                    Toast.makeText(context, "Upload Success", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "onSucess: firebase download url: " + firebaseUrl.toString())
                    dbRef = database.reference
                        .child(context.getString(R.string.dbnode_users))
                        .child(auth.currentUser!!.uid)
                        .child(context.getString(R.string.field_profile_image))
                    dbRef.setValue(firebaseUrl)
                }

            }.addOnFailureListener { exception ->
                Toast.makeText(context, "could not uplaod photo", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "couldn't upload photo ", exception)
            }.addOnProgressListener { taskSnapshot ->
                val currentProgress =
                    (100 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                if (currentProgress > (progress + 15)) {
                    progress = (100 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                    Log.d(TAG, "onProgress Upload is: " + progress + "% done")
                    Toast.makeText(context, "progress" + progress + "%", Toast.LENGTH_SHORT).show();

                }

            }
        } else {
            Toast.makeText(context, "Image is too Large", Toast.LENGTH_SHORT).show()
        }


    }

}