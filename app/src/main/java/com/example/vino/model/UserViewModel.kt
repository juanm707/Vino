package com.example.vino.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vino.network.VineyardManagerUser
import com.example.vino.network.VinoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class VinoApiStatus { LOADING, ERROR, DONE }

class UserViewModel : ViewModel() {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _user = MutableLiveData<VineyardManagerUser>()
    private val _status = MutableLiveData<VinoApiStatus>()

    // The external immutable LiveData for the request status
    val vinoUser: LiveData<VineyardManagerUser> = _user
    val status: LiveData<VinoApiStatus> = _status

    init {
        getUser()
    }


    private fun getUser() {
        viewModelScope.launch {
            _status.value = VinoApiStatus.LOADING
            delay(3000) // to act as slow connection
            try {
                _user.value = VinoApi.retrofitService.getUser()
                _status.value = VinoApiStatus.DONE
            } catch (e: Exception) {
                Log.d("NetworkError", "$e handled!")
                _status.value = VinoApiStatus.ERROR
            }
        }
    }
}