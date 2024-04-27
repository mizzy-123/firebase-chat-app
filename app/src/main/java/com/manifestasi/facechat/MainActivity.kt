package com.manifestasi.facechat

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.Query
import com.manifestasi.facechat.adapter.ListChatAdapter
import com.manifestasi.facechat.databinding.ActivityMainBinding
import com.manifestasi.facechat.firebase.Firestore
import com.manifestasi.facechat.firebase.data.DataChat

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var listChatAdapter: ListChatAdapter
    private lateinit var chatList: ArrayList<DataChat>
    private lateinit var firestore: Firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                            if (status != null) {
                                val dataChat = DataChat(
                                    id = id,
                                    status = status.toInt()
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
    }
}