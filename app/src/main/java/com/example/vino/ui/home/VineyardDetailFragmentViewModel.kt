package com.example.vino.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.example.vino.model.Vineyard
import com.example.vino.network.VinoApiStatus
import com.example.vino.network.WeatherBasic
import com.example.vino.repository.VinoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class VineyardDetailFragmentViewModel(private val repository: VinoRepository) : ViewModel() {

    private val _vineyard = MutableLiveData<Vineyard>()
    private val _weather = MutableLiveData<WeatherBasic>()

    val vineyard: LiveData<Vineyard> = _vineyard
    val weather: LiveData<WeatherBasic> = _weather

    fun setVineyard(vineyard: Vineyard) {
        _vineyard.value = vineyard
        viewModelScope.launch {
            _weather.value = repository.getDailyWeather(vineyard.latitude, vineyard.longitude)
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