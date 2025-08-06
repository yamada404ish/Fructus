package com.example.fructus.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fructus.data.local.dao.FruitDao
import com.example.fructus.data.local.entity.FruitEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// ViewModel that loads and stores a single fruit based on its ID
class DetailViewModel(
    private val fruitDao: FruitDao, // DAO to access the fruit data
    private val fruitId: Int        // ID of the fruit to be displayed
) : ViewModel() {


    // Backing property for the fruit, starts as null
    private val _fruit = MutableStateFlow<FruitEntity?>(null)

    // Public read-only state for UI to observe
    val fruit: StateFlow<FruitEntity?> = _fruit


    init {
        // Start a coroutine to listen to changes from the database
        viewModelScope.launch {
            // Collect the fruit by ID, and update state when it changes
            fruitDao.getFruitById(fruitId).collectLatest { result ->
                _fruit.value = result // Set new fruit data
            }
        }
    }
}


// Factory that creates DetailViewModel and passes DAO and fruitId to it
class DetailViewModelFactory(
    private val fruitDao: FruitDao, // Needed to fetch fruit from database
    private val fruitId: Int        // ID of the selected fruit
) : ViewModelProvider.Factory {

    // Create the ViewModel with required dependencies
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailViewModel(fruitDao, fruitId) as T // Safe cast to correct type
    }
}



/*
This ViewModel is used to fetch and observe a single fruit from the Room database using its id.

The UI collects from fruit: StateFlow<FruitEntity?> to show fruit data.

A factory is used to pass the fruitDao and the fruitId when the ViewModel is created in your DetailScreen.
*/

