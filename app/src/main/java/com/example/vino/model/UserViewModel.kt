package com.example.vino.model

import android.util.Log
import androidx.lifecycle.*
import com.example.vino.network.Todo
import com.example.vino.network.VineyardManagerUser
import com.example.vino.network.VinoApi
import com.example.vino.repository.VinoRepository
import kotlinx.coroutines.*
import java.util.*

enum class VinoApiStatus { LOADING, ERROR, DONE }

class UserViewModel(private val repository: VinoRepository) : ViewModel() {

    private val _user = MutableLiveData<VineyardManagerUser>()
    private val _status = MutableLiveData<VinoApiStatus>()
    private val _todos = MutableLiveData<MutableList<Todo>>()

    val vinoUser: LiveData<VineyardManagerUser> = _user
    val status: LiveData<VinoApiStatus> = _status
    val todos: LiveData<MutableList<Todo>> = _todos

    val completeTodos: LiveData<List<Todo>> = repository.completeTodos.asLiveData()
    val inCompleteTodos:  LiveData<List<Todo>> = repository.inCompleteTodos.asLiveData()

    init {
        getUser() // first thing to do is get user for home fragment
    }

    fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    //delay(3000) // to act as slow connection
    private fun getUser() {
        getDataLoad {
            _user.value = VinoApi.retrofitService.getUser()
        }
    }

    fun getTodos() {
        getDataLoad {
            _todos.value = VinoApi.retrofitService.getTodos() // TODO: might not need this?
            _todos.value!!.forEach { todo ->
                repository.insert(todo)
            }
        }
    }

    fun insertTodo(todo: Todo) = viewModelScope.launch {
        repository.insert(todo)
    }

    private fun getDataLoad(getData: suspend () -> Unit): Job {
        return viewModelScope.launch {
            _status.value = VinoApiStatus.LOADING
            try {
                getData()
                _status.value = VinoApiStatus.DONE
            } catch (e: Exception) {
                Log.d("NetworkError", "$e handled!")
                _status.value = VinoApiStatus.ERROR
            }
        }
    }

    fun setTodoComplete(id: Int) {
        _todos.value?.forEach { todo ->
            if (todo.id == id) {
                //todo.completed = true
                val calendar = Calendar.getInstance()
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.get(Calendar.MONTH) + 1
                //todo.dueDate = "$month/$day"
                insertTodo(Todo(id, todo.job, todo.description, "$month/$day", true)) // TODO instead of inserting just update table
            }
        }
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