package com.example.vino.vinodao

import androidx.room.*
import com.example.vino.model.Block
import com.example.vino.model.Todo

@Dao
interface BlockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(block: Block)

    @Update
    suspend fun update(block: Block)

    @Delete
    suspend fun delete(block: Block)

    @Query("DELETE FROM Todo")
    suspend fun deleteAll()
}