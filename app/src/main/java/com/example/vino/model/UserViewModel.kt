package com.example.vino.model

import android.util.Log
import androidx.lifecycle.*
import coil.memory.MemoryCache
import com.example.vino.network.BlockCoordinates
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
    private val _todoAmount = MutableLiveData(0)
    private val _blocks = MutableLiveData<List<BlockCoordinates>>()

    val vinoUser: LiveData<VineyardManagerUser> = _user
    val apiStatus: LiveData<VinoApiStatus> = _status
    val todos: LiveData<MutableList<Todo>> = _todos
    val todoAmount: LiveData<Int> = _todoAmount // to update the badge
    val blocks: LiveData<List<BlockCoordinates>> = _blocks // current blocks

    val completeTodos: LiveData<List<Todo>> = repository.completeTodos.asLiveData()
    val inCompleteTodos:  LiveData<List<Todo>> = repository.inCompleteTodos.asLiveData()

    var imageCacheKey: MemoryCache.Key? = null // set by selected vineyard on click
    var currentVineyard: Vineyard? = null

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
            _todoAmount.value = 0 // reset
            _todos.value = VinoApi.retrofitService.getTodos()
            _todos.value!!.forEach { todo ->
                insertTodo(todo)
            }
        }
    }

    fun getBlocks() {
        getDataLoad {
            _blocks.value = VinoApi.retrofitService.getBlocks()
            _blocks.value!!.forEach { block ->
                insertBlock(Block(block.id, block.vineyardId, block.name))
            }

        }
    }

    fun insertTodo(todo: Todo) = viewModelScope.launch {
        repository.insert(todo)
        if (!todo.completed)
            _todoAmount.value = _todoAmount.value?.plus(1)
    }

    fun insertBlock(block: Block) = viewModelScope.launch {
        repository.insert(block)
    }

    fun updateTodo(todo: Todo) = viewModelScope.launch {
        todo.completed = true
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        todo.dueDate = "$month/$day"
        repository.update(todo)
        _todoAmount.value = _todoAmount.value?.minus(1)
    }

    fun deleteTodo(todo: Todo) = viewModelScope.launch {
        repository.delete(todo)
        if (!todo.completed)
            _todoAmount.value = _todoAmount.value?.minus(1)
    }

    fun setSelectedVineyard(id: Int) {
        currentVineyard = _user.value?.vineyards?.find {
            it.id == id
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

class UserViewModelFactory(private val repository: VinoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}