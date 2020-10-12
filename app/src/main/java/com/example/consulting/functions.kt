package com.example.consulting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import java.io.ByteArrayOutputStream


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