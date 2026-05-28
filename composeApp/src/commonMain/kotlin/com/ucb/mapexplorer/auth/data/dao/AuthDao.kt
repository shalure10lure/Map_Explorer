package com.ucb.mapexplorer.auth.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ucb.mapexplorer.auth.data.entity.UserEntity

@Dao
interface AuthDao {
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getSession(): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun deleteSession()
}