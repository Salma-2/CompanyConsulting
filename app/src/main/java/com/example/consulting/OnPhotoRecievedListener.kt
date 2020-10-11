package com.example.consulting

import android.graphics.Bitmap
import android.net.Uri

interface OnPhotoRecievedListener {
    fun getImagePath(imagePath: Uri)
    fun getImageBitmap(bitmap: Bitmap);
}