package com.manifestasi.facechat.firebase

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Firestore private constructor() {
    val db = Firebase.firestore

    companion object {
        val instance: Firestore by lazy { Firestore() }
    }

    fun getCollection(): CollectionReference {
        return db.collection("chat")
    }

    fun getDatabase(): FirebaseFirestore {
        return db
    }
}