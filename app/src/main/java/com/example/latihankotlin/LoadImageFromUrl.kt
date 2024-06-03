package com.example.latihankotlin

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

suspend fun LoadImageFromUrl(urlString: String):Bitmap?{
    return withContext(Dispatchers.IO){
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            BitmapFactory.decodeStream(inputStream)
        }catch (e:Exception){
            e.printStackTrace()
            null
        }
    }
}