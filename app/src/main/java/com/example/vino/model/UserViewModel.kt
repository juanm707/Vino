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
    private val _blocks = MutableLiveData<List<BlockWithCoordinates>>()

    val vinoUser: LiveData<VineyardManagerUser> = _user
    val apiStatus: LiveData<VinoApiStatus> = _status

    var blocks: LiveData<List<BlockWithCoordinates>> = _blocks // current blocks

    val completeTodos: LiveData<List<Todo>> = repository.completeTodos.asLiveData()
    val inCompleteTodos:  LiveData<List<Todo>> = repository.inCompleteTodos.asLiveData()

    var imageCacheKey: MemoryCache.Key? = null // set by selected vineyard on click
    var currentVineyard: Vineyard? = null

    init {
        refreshUser() // first thing to do is get user for home fragment
    }

    fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    private fun refreshUser() {
        getDataLoad {
            _user.value = repository.refreshUser()
        }
    }

    fun refreshTodos() {
        getDataLoad {
            repository.refreshTodos()
        }
    }

    fun refreshBlocks(id: Int) {
        getDataLoad {
            repository.refreshBlocks()
            _blocks.value = repository.getBlocksForVineyardId(id)
        }
    }

    fun insertTodo(todo: Todo) = viewModelScope.launch {
        repository.insert(todo)
    }

    fun insertBlock(block: Block) = viewModelScope.launch {
        repository.insert(block)
    }

    fun updateTodo(todo: Todo) = viewModelScope.launch {
        todo.completed = true
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        todo.dueDate = calendar.timeInMillis
        repository.update(todo)
    }

    fun deleteTodo(todo: Todo) = viewModelScope.launch {
        repository.delete(todo)
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
                //delay(3000) // for slow connection
                getData()
                _status.value = VinoApiStatus.DONE
            } catch (e: Exception) {
                Log.d("NetworkError", "$e handled!")
                _status.value = VinoApiStatus.ERROR
            }
        }
    }

    fun getBlockForName(name: String?): Block? {
        return if (name == null)
            null
        else {
            _blocks.value?.find { parentBlock ->
                parentBlock.block.name == name
            }?.block
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