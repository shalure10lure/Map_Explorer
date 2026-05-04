package com.ucb.mapexplorer.auth.data.datasource

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await


actual class FirebaseManager actual constructor() {

    private val database = FirebaseDatabase.getInstance().reference

    actual suspend fun saveData(path: String, value: String) {
        try {
            database.child(path).setValue(value).await()
            println("Firebase Android: Guardado exitoso en $path")
        } catch (e: Exception) {
            println("Firebase Android: Error - ${e.message}")
        }
    }
    actual suspend fun getData(path: String): String? {
        return try {
            val snapshot = database.child(path).get().await()
            snapshot.getValue(String::class.java)
        } catch (e: Exception) {
            println("Firebase Android: Error leyendo - ${e.message}")
            null
        }
    }
}
