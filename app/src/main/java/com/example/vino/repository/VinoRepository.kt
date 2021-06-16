package com.example.vino.repository

import androidx.annotation.WorkerThread
import com.example.vino.network.Todo
import com.example.vino.vinodao.TodoDao
import kotlinx.coroutines.flow.Flow

class VinoRepository(private val todoDao: TodoDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val completeTodos: Flow<List<Todo>> = todoDao.getCompleteTodos()
    val inCompleteTodos: Flow<List<Todo>> = todoDao.getIncompleteTodos()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(todo: Todo) {
        return todoDao.insert(todo)
    }
}