package com.example.growpath.data.local

import androidx.room.*
import com.example.growpath.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("UPDATE users SET experience = experience + :amount WHERE id = :userId")
    suspend fun addExperience(userId: String, amount: Int)

    @Query("UPDATE users SET level = :newLevel WHERE id = :userId")
    suspend fun updateLevel(userId: String, newLevel: Int)
}
