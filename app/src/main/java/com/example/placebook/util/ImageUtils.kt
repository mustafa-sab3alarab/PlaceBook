package com.example.placebook.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

object ImageUtils {

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, filename: String) {

        // ByteArrayOutputStream is created to hold the image data.
        val stream = ByteArrayOutputStream()

        /*
          You write the image bitmap to the stream object using the lossless PNG format.
          Note that the second parameter is a quality setting, but it's ignored for the PNG
          format.
        */
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        // The stream is converted into an array of bytes.
        val bytes = stream.toByteArray()

        // saveBytesToFile() is called to write the bytes to a file.
        saveBytesToFile(context, bytes, filename)

    }

    /*
        saveBytesToFile() takes in a Context, ByteArray, and a String for the
        filename and saves the bytes to a file.
    */
    private fun saveBytesToFile(context: Context, bytes: ByteArray, filename: String) {


        val outputStream: FileOutputStream
        try {

            /*
            openFileOutput is used to open a FileOutputStream using the given filename.
            The Context.MODE_PRIVATE flag causes the file to be written in the private area
            where only the PlaceBook app can access it
            */
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)

            // The bytes are written to the outputStream and then the stream is closed.
            outputStream.write(bytes)
            outputStream.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun loadBitmapFromFile(context: Context, filename: String): Bitmap? {
        val filePath = File(context.filesDir, filename).absolutePath
        return BitmapFactory.decodeFile(filePath)
    }
}