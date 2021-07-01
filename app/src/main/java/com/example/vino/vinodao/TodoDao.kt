package com.example.vino.vinodao

import androidx.room.*
import com.example.vino.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM Todo WHERE completed = 0")
    fun getIncompleteTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM Todo WHERE completed = 1")
    fun getCompleteTodos(): Flow<List<Todo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Query("DELETE FROM Todo")
    suspend fun deleteAll()
}