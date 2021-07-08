package com.example.vino.model

import android.util.Log
import androidx.lifecycle.*
import coil.memory.MemoryCache
import com.example.vino.network.BlockCoordinates
import com.example.vino.network.VineyardManagerUser
import com.example.vino.network.VinoApi
import com.example.vino.network.VinoApiStatus
import com.example.vino.repository.VinoRepository
import kotlinx.coroutines.*
import java.util.*

class UserViewModel(private val repository: VinoRepository) : ViewModel() {
    private val _status = MutableLiveData<VinoApiStatus>()
    private val _user = MutableLiveData<VineyardManagerUser>()

    val vinoUser: LiveData<VineyardManagerUser> = _user
    val apiStatus: LiveData<VinoApiStatus> = _status

    val completeTodos: LiveData<List<Todo>> = repository.completeTodos.asLiveData()
    val inCompleteTodos:  LiveData<List<Todo>> = repository.inCompleteTodos.asLiveData()

    var imageCacheKey: MemoryCache.Key? = null // set by selected vineyard on click

    init {
        refreshUser() // first thing to do is get user for home fragment
    }

    private fun refreshUser() {
        getDataLoad {
            repository.refreshUser()
            _user.value = repository.getUser()
        }
    }

    fun refreshTodos() {
        getDataLoad {
            repository.refreshTodos()
        }
    }

    fun insertTodo(todo: Todo) = viewModelScope.launch {
        repository.insert(todo)
    }

    fun updateTodo(todo: Todo) = viewModelScope.launch {
        todo.completed = true
        val calendar = Calendar.getInstance()
        todo.dueDate = calendar.timeInMillis
        repository.update(todo)
    }

    fun deleteTodo(todo: Todo) = viewModelScope.launch {
        repository.delete(todo)
    }

    private fun getDataLoad(getData: suspend () -> Unit): Job {
        return viewModelScope.launch {
            _status.value = VinoApiStatus.LOADING
            try {
                //delay(5000) // for slow connection
                getData()
                _status.value = VinoApiStatus.DONE
            } catch (e: Exception) {
                Log.d("NetworkError", "$e handled!")
                _status.value = VinoApiStatus.ERROR
            }
        }
    }

    fun sortTodoByDate(todoList: List<Todo>): MutableList<Todo> {
        return todoList.sortedBy { todo ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = todo.dueDate
            return@sortedBy calendar.time
        }.toMutableList()
    }
}

class UserViewModelFactory(private val repository: VinoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}