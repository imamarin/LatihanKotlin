package com.example.latihankotlin

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

suspend fun getRequest(urlString: String): Result<String> {
    return withContext(Dispatchers.IO) {
        try {
            var url = URL(urlString)
            var redirect = false
            var response = StringBuilder()
            do {
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.instanceFollowRedirects = false
                urlConnection.requestMethod = "GET"
                urlConnection.connect()

                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                    val newUrl = urlConnection.getHeaderField("Location")
                    url = URL(newUrl)
                    redirect = true
                } else {
                    redirect = false
                    val inputStream = if(responseCode in 200..299){
                        urlConnection.inputStream
                    }else{
                        urlConnection.errorStream
                    }
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    if(responseCode !in 200..299){
                        return@withContext Result.failure(Exception("HTTP Error Code: $responseCode\n$response"))
                    }
                }
                urlConnection.disconnect()
            } while (redirect)

            Result.success(response.toString())
        }catch (e: Exception){
            Result.failure(e)
        }

    }
}