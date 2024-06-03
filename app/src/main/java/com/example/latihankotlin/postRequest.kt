package com.example.latihankotlin

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

suspend fun postRequest(urlString: String, jsonObject: JSONObject): Result<String> {

    return withContext(Dispatchers.IO) {
        try {
            var url = URL(urlString)
            var redirect = false
            var response = StringBuilder()

            do {
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.instanceFollowRedirects = false
                urlConnection.requestMethod = "POST"
                urlConnection.setRequestProperty("Content-Type", "application/json")
                urlConnection.setRequestProperty("Accept", "application/json")
                urlConnection.doOutput = true

                val outputStreamWriter = OutputStreamWriter(urlConnection.outputStream)
                outputStreamWriter.write(jsonObject.toString())
                outputStreamWriter.flush()
                outputStreamWriter.close()

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
                        return@withContext Result.failure(Exception("HTTP error code: $responseCode\n$response"))
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
