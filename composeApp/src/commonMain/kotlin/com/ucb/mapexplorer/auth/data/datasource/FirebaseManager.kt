package com.ucb.mapexplorer.auth.data.datasource

expect class FirebaseManager() {
    suspend fun saveData(path: String, value: String)
    suspend fun getData(path: String): String?
}