package com.example.memories.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.example.memories.repository.MemoriesRepository
import com.example.memories.room.MemoriesDB
import com.example.memories.room.Memory
import kotlinx.coroutines.launch

class MemoriesViewModel(app:Application) : AndroidViewModel(app) {

    private val db = MemoriesDB.getDatabase(app)
    private val repository = MemoriesRepository(db)

    val memoriesList = repository.getAlMemories()
    var imageUri = Uri.parse("")



    fun insertMemory(memory: Memory) =
        viewModelScope.launch {
            repository.insertMemory(memory)
        }

    fun deleteMemory(memory: Memory) =
        viewModelScope.launch {
            repository.deleteMemory(memory)
        }


    class MemoriesViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(MemoriesViewModel::class.java)) {
                MemoriesViewModel(application) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}