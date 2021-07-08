package com.example.vino.vinodao

import androidx.room.*
import com.example.vino.model.Block
import com.example.vino.network.VineyardManagerUser

@Dao
interface UserDao {

    @Query("SELECT * FROM User")
    suspend fun getUser(): VineyardManagerUser

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: VineyardManagerUser)

    @Update
    suspend fun update(user: VineyardManagerUser)

    @Delete
    suspend fun delete(user: VineyardManagerUser)

    @Query("DELETE FROM Block")
    suspend fun deleteAll()
}