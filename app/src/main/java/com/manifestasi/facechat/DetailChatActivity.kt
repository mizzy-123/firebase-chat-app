package com.manifestasi.facechat

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.Query
import com.manifestasi.facechat.adapter.ListChatDetailAdapter
import com.manifestasi.facechat.databinding.ActivityDetailChatBinding
import com.manifestasi.facechat.firebase.Firestore
import com.manifestasi.facechat.firebase.data.DataDetailChat
import com.manifestasi.facechat.firebase.data.DataStatusChat

class DetailChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailChatBinding
    private lateinit var listChatDetailAdapter: ListChatDetailAdapter
    private lateinit var arrayChatDetail: ArrayList<DataDetailChat>
    private lateinit var firestore: Firestore
    private var id = ""
    private var timeStamp: Long? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        arrayChatDetail = ArrayList()
        firestore = Firestore.instance
        val bundle: Bundle? = intent.extras
        if (bundle != null){
            id = bundle.getString(EXTRA_ID, "")
            timeStamp = bundle.getLong(EXTRA_TIMESTAMP)
            getDataFromFirebase()

            binding.imageviewsendmessage.setOnClickListener {
                postDataToFirebase()
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }


    }

    private fun postDataToFirebase(){
        val message = binding.etSendChat.text.toString()
        val timestamp = System.currentTimeMillis()
        binding.imageviewsendmessage.isEnabled = false
        firestore.getCollection()
            .document(id)
            .collection("message")
            .add(DataDetailChat(
                user = "mobile",
                message = message,
                timestamp = timestamp
            ))
            .addOnSuccessListener {
                binding.imageviewsendmessage.isEnabled = true
                binding.etSendChat.setText("")

                val recycler = binding.rvChatBubble
                recycler.post {
                    recycler.scrollToPosition(arrayChatDetail.lastIndex)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                binding.imageviewsendmessage.isEnabled = true
            }

        binding.etSendChat.setText("")

    }

    private fun getDataFromFirebase(){
        firestore.getCollection()
            .document(id)
            .collection("message")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Tangani kesalahan saat mendapatkan snapshot
                    Log.e("Firestore", "Error getting documents: $error")
                    return@addSnapshotListener
                }

                if (snapshot != null){
                    arrayChatDetail.clear()
                    for (document in snapshot.documents){
                        val data = document.data

                        if (data != null){
                            val dataDetailChat =  DataDetailChat(
                                user = data["user"].toString(),
                                message = data["message"].toString(),
                                timestamp = data["timestamp"].toString().trim().toLong()
                            )
                            arrayChatDetail.add(dataDetailChat)
                        }
                    }

                    showRecyclerList()
                }
            }
    }

    private fun showRecyclerList(){
        binding.username.text = id

        val recycler = binding.rvChatBubble
        val layoutManager = LinearLayoutManager(this)
        recycler.layoutManager = layoutManager
        listChatDetailAdapter = ListChatDetailAdapter(arrayChatDetail)
        recycler.adapter = listChatDetailAdapter
        recycler.post {
            recycler.scrollToPosition(arrayChatDetail.lastIndex)
        }
    }

    override fun onStop() {
        super.onStop()
        if (timeStamp != null){
            firestore.getCollection()
                .document(id)
                .set(DataStatusChat(
                    status = 0,
                    timestamp = timeStamp!!
                ))
        }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_TIMESTAMP = "extra_timestamp"
    }
}