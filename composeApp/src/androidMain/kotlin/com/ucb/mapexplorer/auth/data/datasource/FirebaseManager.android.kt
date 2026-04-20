package com.ucb.mapexplorer.auth.data.datasource

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await


actual class FirebaseManager actual constructor() {

    private val db = FirebaseDatabase.getInstance().reference

    actual suspend fun saveData(path: String, value: String) {
        db.child(path).setValue(value).await()
    }

    actual suspend fun getData(path: String): String? {
        return try {
            val snapshot = db.child(path).get().await()
            snapshot.getValue(String::class.java)
        } catch (e: Exception) {
            null
        }
    }
}