package com.example.vino.ui.home

import androidx.lifecycle.*
import com.example.vino.model.Vineyard
import com.example.vino.network.WeatherBasic
import com.example.vino.repository.VinoRepository
import kotlinx.coroutines.launch

class WeatherDetailViewModel(private val repository: VinoRepository) : ViewModel() {

    private val _vineyard = MutableLiveData<Vineyard>()
    private val _weather = MutableLiveData<WeatherBasic>()
    private val _whiteText = MutableLiveData(false)

    val vineyard: LiveData<Vineyard> = _vineyard
    val weather: LiveData<WeatherBasic> = _weather
    val isWhiteText: LiveData<Boolean> = _whiteText

    fun setVineyard(vineyard: Vineyard) {
        _vineyard.value = vineyard
        viewModelScope.launch {
            _weather.value = repository.getAdvancedWeather(vineyard.latitude, vineyard.longitude)
        }
    }

    fun setWhiteText(setWhiteText: Boolean) {
        _whiteText.value = setWhiteText
    }
}

class WeatherDetailViewModelFactory(private val repository: VinoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}