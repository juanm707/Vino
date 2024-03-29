package com.example.vino.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.example.vino.model.Block
import com.example.vino.model.BlockWithCoordinates
import com.example.vino.model.Vineyard
import com.example.vino.network.VinoApiStatus
import com.example.vino.repository.VinoRepository
import kotlinx.coroutines.*

class VineyardMapFragmentViewModel(private val repository: VinoRepository) : ViewModel() {
    private val _status = MutableLiveData<VinoApiStatus>()
    private val _vineyard = MutableLiveData<Vineyard>()
    private val _blocks = MutableLiveData<List<BlockWithCoordinates>>()

    val apiStatus: LiveData<VinoApiStatus> = _status
    val vineyard: LiveData<Vineyard> = _vineyard
    val blocks: LiveData<List<BlockWithCoordinates>> = _blocks

    fun setVineyard(vineyardId: Int) {
        viewModelScope.launch {
            _vineyard.value = repository.getVineyard(vineyardId)
        }
    }

    fun refreshBlocks(vineyardId: Int) {
        getDataLoad {
            repository.refreshBlocks()
            val unsortedCoordinateBlocks = repository.getBlocksForVineyardId(vineyardId)

            withContext(Dispatchers.Default) {
                unsortedCoordinateBlocks.forEach { block ->
                    val sortedCoordinates =
                        block.coordinates.sortedBy { coordinate -> coordinate.coordinateId }
                    block.coordinates = sortedCoordinates
                }
            }

            _blocks.value = unsortedCoordinateBlocks
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

class VineyardMapFragmentViewModelFactory(private val repository: VinoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VineyardMapFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VineyardMapFragmentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}