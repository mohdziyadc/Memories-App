package com.example.memories.repository

import com.example.memories.room.MemoriesDB
import com.example.memories.room.Memory

class MemoriesRepository(
   private val db:MemoriesDB
) {

    fun getAlMemories() = db.getMemoriesDAO().getAllPlaces()

    suspend fun insertMemory(memory: Memory) = db.getMemoriesDAO().insertMemory(memory)

    suspend fun deleteMemory(memory: Memory) = db.getMemoriesDAO().deleteMemory(memory)
}