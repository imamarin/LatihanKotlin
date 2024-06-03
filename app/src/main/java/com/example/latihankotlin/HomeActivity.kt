package com.example.latihankotlin

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class HomeActivity : AppCompatActivity() {

    private lateinit var adapterItem: ItemAdapter
    private lateinit var listView: ListView
    private lateinit var listUser: MutableList<Item>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listView = findViewById(R.id.daftarUser)

        lifecycleScope.launch {
            val result = getRequest("http://reqres.in/api/users?page=2")
            listUser = mutableListOf<Item>()
            result.fold(
                onSuccess = { response->

                    val jsonObject = JSONObject(response.toString())
                    val jsonArray = JSONArray(jsonObject.getString("data").toString())

                    for (i in 0 until jsonArray.length()){
                        val data = jsonArray.getJSONObject(i)
                        listUser.add(Item(data.getString("first_name").toString(), data.getString(("avatar").toString())))
                    }

                    adapterItem = ItemAdapter(applicationContext, listUser)
                    listView.adapter = adapterItem
                },
                onFailure = { error ->
                    println("Error Home $error")
                }
            )
        }

        val searchView = findViewById<SearchView>(R.id.filterUser);
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                adapterItem.filter.filter(newText)
                return true
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
        })
    }
}