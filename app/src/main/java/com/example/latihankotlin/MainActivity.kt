package com.example.latihankotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tombol = findViewById<Button>(R.id.tombol)
        val username: EditText = findViewById(R.id.username)
        val password: EditText = findViewById(R.id.password)





        tombol.setOnClickListener{
//            println(username.text.toString())

            val jsonObject = JSONObject().apply {
                put("email", username.text.toString())
                put("password", password.text.toString())
            }

            lifecycleScope.launch {

                val result = postRequest("http://reqres.in/api/login", jsonObject)
                result.fold(
                    onSuccess = {response->

                        val jsonObject2 = JSONObject(response.toString());

                        if(jsonObject2.getString("token").toString().isNotEmpty()){
                            val intent = Intent(applicationContext, HomeActivity::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(applicationContext,"Login Anda gagal ", Toast.LENGTH_LONG ).show()
                        }
                    },
                    onFailure = {error->
                        println("Login Anda Gagal ")
                    }
                )

            }

        }

    }



}