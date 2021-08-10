package com.example.vino.ui.dashboard

import androidx.lifecycle.*
import com.example.vino.model.Todo
import com.example.vino.model.Vineyard
import com.example.vino.repository.VinoRepository
import kotlinx.coroutines.launch
import java.util.*

class DashboardFragmentViewModel(private val repository: VinoRepository) : ViewModel() {

    private val _sprayCount: MutableLiveData<Int> = MutableLiveData(0)
    val sprayCount: LiveData<Int> = _sprayCount

    private val _sprayedVineyardNames: MutableLiveData<String> = MutableLiveData()
    val sprayedVineyardNames: LiveData<String> = _sprayedVineyardNames

    private val _sprayedVineyards: MutableLiveData<List<Vineyard>> = MutableLiveData()
    val sprayedVineyards: LiveData<List<Vineyard>> = _sprayedVineyards


    fun getSprayCount() {
        viewModelScope.launch {
            _sprayCount.value = repository.getNumberOfVineyardsSprayed()
            _sprayedVineyards.value = repository.getVineyardsSprayed()
            _sprayedVineyardNames.value = getSprayedTextPreview(_sprayedVineyards.value)
        }
    }

    private fun getSprayedTextPreview(vineyardsSprayed: List<Vineyard>?): String {
        return if (vineyardsSprayed.isNullOrEmpty())
            "No sprays"
        else if (vineyardsSprayed.size == 1)
            vineyardsSprayed[0].name
        else {
            "${vineyardsSprayed[0].name}, ${vineyardsSprayed[1].name}${moreThanTwoSprayed(vineyardsSprayed)}"
        }
    }

    private fun moreThanTwoSprayed(vineyardsSprayed: List<Vineyard>): String {
        return if (vineyardsSprayed.size == 2)
            ""
        else
            " + ${_sprayCount.value?.minus(2)} more"
    }

    fun getTodosDueTodayCount(todoList: List<Todo>): Int {
        var count = 0
        todoList.forEach { todo ->
            if (isTodoDueToday(todo))
                count++
        }
        return count
    }

    private fun isTodoDueToday(todo: Todo): Boolean {
        val c = Calendar.getInstance(TimeZone.getDefault())

        c.set(Calendar.HOUR_OF_DAY, 0); //set hour to last hour
        c.set(Calendar.MINUTE, 0); //set minutes to last minute
        c.set(Calendar.SECOND, 0); //set seconds to last second
        c.set(Calendar.MILLISECOND, 0); //set milliseconds to last millisecond
        val today = c.timeInMillis

        //c.add(Calendar.DAY_OF_MONTH, 1); //add a day
        c.set(Calendar.HOUR_OF_DAY, 23); //set hour to last hour
        c.set(Calendar.MINUTE, 59); //set minutes to last minute
        c.set(Calendar.SECOND, 59); //set seconds to last second
        c.set(Calendar.MILLISECOND, 999); //set milliseconds to last millisecond
        val tomorrow = c.timeInMillis

        return ((todo.dueDate in today..tomorrow))
    }

// This is another way to observe but we need to store value and remove suspend from dao and repo and add livedata as return value
//    fun getSprayCount(): LiveData<Int> {
//        return repository.getNumberOfVineyardsSprayed()
//    }
}

class DashboardFragmentViewModelFactory(private val repository: VinoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardFragmentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}