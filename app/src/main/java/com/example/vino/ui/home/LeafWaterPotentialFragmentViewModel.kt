package com.example.vino.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.example.vino.model.LWPReading
import com.example.vino.model.Vineyard
import com.example.vino.network.VinoApiStatus
import com.example.vino.repository.VinoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LeafWaterPotentialFragmentViewModel(private val repository: VinoRepository) : ViewModel() {
    private val _status = MutableLiveData<VinoApiStatus>()
    private val _lwpReadings = MutableLiveData<MutableMap<String, List<LWPReading>>>()
    private val _vineyardName = MutableLiveData<String>()

    val apiStatus: LiveData<VinoApiStatus> = _status
    var lwpReadings: LiveData<MutableMap<String, List<LWPReading>>> = _lwpReadings
    val vineyardName: LiveData<String> = _vineyardName

    fun refreshLWPReadings(vineyardId: Int) {
        val lwpReadingsList = mutableMapOf<String, List<LWPReading>>()
        _lwpReadings.value = lwpReadingsList // clear it

        getDataLoad {
            repository.refreshLWPReadings()
            repository.refreshBlocks()
            _vineyardName.value = repository.getVineyard(vineyardId).name
            val blocks = repository.getBlockInfoForLWPReading(vineyardId)
            blocks.forEach { block ->
                lwpReadingsList[block.blockName] = repository.getLWPReadingsForBlockId(block.blockId)
            }
            _lwpReadings.value = lwpReadingsList
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
}

class LeafWaterPotentialFragmentViewModelFactory(private val repository: VinoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeafWaterPotentialFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LeafWaterPotentialFragmentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}