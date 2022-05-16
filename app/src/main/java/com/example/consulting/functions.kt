package com.example.consulting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import com.example.consulting.adapters.ChatAdapter
import com.example.consulting.models.Chatroom
import com.example.consulting.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nostra13.universalimageloader.core.ImageLoader
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


fun isEmpty(str: String): Boolean {
    return str.equals("")
}


 fun isValidDomain(email: String): Boolean {
    val startIndex = email.indexOf("@") + 1
    val domain = email.substring(startIndex)
    return domain.equals(DOMAIN_NAME)
}


fun <T> navigateTo(context: Context, activity: Activity, to: Class<T>, isFinishid: Boolean = false){
    val intent = Intent(context, to)
    if(isFinishid)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)

    if(isFinishid)
        activity.finish()
}

fun showProgressBar(view: View){
    view.visibility= View.VISIBLE
}

fun dismissProgressBar(view: View){
    if(view.visibility == View.VISIBLE)
        view.visibility= View.INVISIBLE
}

fun getBytesFromBitmap(bitmap: Bitmap, quality: Int): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
    return stream.toByteArray()
}

fun getTimeStamp(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("Canada/Pacific")
    return sdf.format(Date())
}

