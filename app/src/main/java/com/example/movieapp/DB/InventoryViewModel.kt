package com.example.movieapp.DB

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.movieapp.API.MovieItem
import kotlinx.coroutines.launch

class InventoryViewModel(private val itemDao: ItemDao):ViewModel() {

    val allItems: LiveData<List<Item>> = itemDao.getItems().asLiveData()
    val allCartelera: LiveData<List<Cartelera>> = itemDao.getCantidad().asLiveData()

    private fun insertItem(item: Item){ viewModelScope.launch { itemDao.insertItem(item) } }
    private fun updateItem(item: Item){ viewModelScope.launch { itemDao.updateItem(item) } }
    fun deleteItem(item: Item){ viewModelScope.launch { itemDao.deleteItem(item) } }

    private fun insertCartelera(item: Cartelera){ viewModelScope.launch { itemDao.insertCartelera(item) } }
    private fun updateCartelera(item: Cartelera){ viewModelScope.launch { itemDao.updateCartelera(item) } }
    fun deleteCartelera(item: Cartelera){ viewModelScope.launch { itemDao.deleteCartelera(item) } }

    fun addNewItem(page:Int,poster:String,overview:String,title:String,backdrop:String,voto:Float){
        val newItem = Item(page,poster,overview,title,backdrop,voto)
        insertItem(newItem)
    }

    fun addNewItemCartelera(poster:String,overview:String,title:String,backdrop:String,voto:Float){
        val newItem = Cartelera(poster,overview,title,backdrop,voto)
        insertCartelera(newItem)
    }
}

class InventoryViewModelFactory(private val itemDao: ItemDao): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}