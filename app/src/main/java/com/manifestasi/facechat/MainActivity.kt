package com.manifestasi.facechat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.Query
import com.manifestasi.facechat.adapter.ListChatAdapter
import com.manifestasi.facechat.databinding.ActivityMainBinding
import com.manifestasi.facechat.firebase.Firestore
import com.manifestasi.facechat.firebase.data.DataChat
import com.manifestasi.facechat.firebase.data.DataStatusChat

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var listChatAdapter: ListChatAdapter
    private lateinit var chatList: ArrayList<DataChat>
    private lateinit var firestore: Firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestRuntimePermission()
        firestore = Firestore.instance
        chatList = ArrayList()

        firestore.getCollection()
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    // Tangani kesalahan saat mendapatkan snapshot
                    Log.e("Firestore", "Error getting documents: $error")
                    return@addSnapshotListener
                }

                if (value != null) {
                    chatList.clear()
                    for (document in value.documents) {
                        val id = document.id
                        val data = document.data

                        // Periksa jika data tidak null
                        if (data != null) {
                            // Coba mengonversi nilai status ke Integer
                            val status = data["status"] as? Long
                            val timestamp = data["timestamp"]
                            if (status != null) {
                                val dataChat = DataChat(
                                    id = id,
                                    status = status.toInt(),
                                    timestamp = timestamp.toString().trim().toLong()
                                )
                                chatList.add(dataChat)
                            } else {
                                Log.e("Firestore", "Invalid status value for document with ID: $id")
                            }
                        } else {
                            Log.e("Firestore", "Document data is null for document with ID: $id")
                        }
                    }
                    // Setelah loop selesai, panggil metode untuk menampilkan daftar
                    showRecyclerList()
                } else {
                    Log.e("Firestore", "Snapshot value is null")
                }
            }
    }

    private fun showRecyclerList(){
        val recycler = binding.rvChat
        recycler.layoutManager = LinearLayoutManager(this)
        listChatAdapter = ListChatAdapter(chatList)
        recycler.adapter = listChatAdapter

        listChatAdapter.setOnClickCallback(object : ListChatAdapter.OnItemClickCallback {
            override fun onItemClicked(data: DataChat, position: Int) {
                val intent = Intent(this@MainActivity, DetailChatActivity::class.java)
                intent.putExtra(DetailChatActivity.EXTRA_ID, data.id)
                intent.putExtra(DetailChatActivity.EXTRA_TIMESTAMP, data.timestamp)
                startActivity(intent)
            }

        })
    }

    private fun requestRuntimePermission(): Boolean{
        if (checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SYSTEM_ALERT_WINDOW), 121)
            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 121){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}