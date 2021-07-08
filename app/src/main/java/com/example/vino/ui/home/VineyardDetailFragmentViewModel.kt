package com.example.vino.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.example.vino.model.Vineyard
import com.example.vino.network.VinoApiStatus
import com.example.vino.repository.VinoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class VineyardDetailFragmentViewModel(private val repository: VinoRepository) : ViewModel() {

    private val _vineyard = MutableLiveData<Vineyard>()

    val vineyard: LiveData<Vineyard> = _vineyard

    fun setVineyard(vineyardId: Int) {
        viewModelScope.launch {
            _vineyard.value = repository.getVineyard(vineyardId)
        }
    }
}

class VineyardDetailFragmentViewModelFactory(private val repository: VinoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VineyardDetailFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VineyardDetailFragmentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}