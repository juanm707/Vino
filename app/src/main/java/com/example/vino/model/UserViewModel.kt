package com.example.vino.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vino.network.Todo
import com.example.vino.network.VineyardManagerUser
import com.example.vino.network.VinoApi
import kotlinx.coroutines.*
import java.util.*

enum class VinoApiStatus { LOADING, ERROR, DONE }

class UserViewModel : ViewModel() {

    private val _user = MutableLiveData<VineyardManagerUser>()
    private val _status = MutableLiveData<VinoApiStatus>()
    private val _todos = MutableLiveData<MutableList<Todo>>()

    val vinoUser: LiveData<VineyardManagerUser> = _user
    val status: LiveData<VinoApiStatus> = _status
    val todos: LiveData<MutableList<Todo>> = _todos

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
            _todos.value = VinoApi.retrofitService.getTodos()
        }
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
                todo.completed = true
                val calendar = Calendar.getInstance()
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.get(Calendar.MONTH) + 1
                todo.dueDate = "$month/$day"
            }
        }
    }
}