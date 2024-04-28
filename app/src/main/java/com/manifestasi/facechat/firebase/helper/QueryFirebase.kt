package com.manifestasi.facechat.firebase.helper

import android.util.Log
import com.google.firebase.firestore.Query
import com.manifestasi.facechat.firebase.Firestore

object QueryFirebase {
    fun getLastChatUser(id: String, callback: (String, String) -> Unit) {
        val instance = Firestore.instance
        instance.getCollection()
            .document(id)
            .collection("message")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Tangani kesalahan saat mendapatkan snapshot
                    Log.e("Firestore", "Error getting documents last chat user: $error")
                    callback("", "") // Panggil callback dengan nilai default jika terjadi kesalahan
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val document = snapshot.documents[0]
                    val data = document.data
                    val message = data?.get("message")?.toString() ?: ""
                    val user = data?.get("user")?.toString() ?: ""
                    Log.e("Firestore", "lastchat berhasil: $message")
                    callback(message, user) // Panggil callback dengan nilai message yang diperoleh
                } else {
                    callback("", "") // Panggil callback dengan nilai default jika tidak ada data
                }
            }
    }
}