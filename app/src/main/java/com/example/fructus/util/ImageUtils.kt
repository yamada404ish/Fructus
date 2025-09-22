package com.example.fructus.util

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream


fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, fileName: String): String {
    val directory = File(context.filesDir, "captured_images")
    if (!directory.exists()) directory.mkdirs()

    val file = File(directory, "$fileName.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return file.absolutePath
}
