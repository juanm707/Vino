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

enum class VinoApiStatus { LOADING, ERROR, DONE }

class UserViewModel : ViewModel() {

    private val _user = MutableLiveData<VineyardManagerUser>()
    private val _status = MutableLiveData<VinoApiStatus>()
    private val _todos = MutableLiveData<List<Todo>>()

    val vinoUser: LiveData<VineyardManagerUser> = _user
    val status: LiveData<VinoApiStatus> = _status
    val todos: LiveData<List<Todo>> = _todos

    init {
        getUser() // first thing to do is get user for home fragment
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
}