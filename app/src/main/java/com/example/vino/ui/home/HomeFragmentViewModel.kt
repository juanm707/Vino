package com.example.vino.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.example.vino.model.Vineyard
import com.example.vino.network.VinoApiStatus
import com.example.vino.repository.VinoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeFragmentViewModel(private val repository: VinoRepository) : ViewModel() {

    private val _status = MutableLiveData<VinoApiStatus>()
    private val _vineyards = MutableLiveData<List<Vineyard>>()

    val apiStatus: LiveData<VinoApiStatus> = _status
    val vineyards: LiveData<List<Vineyard>> = _vineyards

    fun refreshVineyards(userId: Int) {
        getDataLoad {
            repository.refreshVineyards(userId)
            _vineyards.value = repository.getVineyards(userId)
        }
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

    fun sortVineyardsByName(vineyards: List<Vineyard>): List<Vineyard> {
        return vineyards.sortedBy { vineyard ->
            vineyard.name
        }
    }
}

class HomeFragmentViewModelFactory(private val repository: VinoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeFragmentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}