package com.example.memories.room

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface MemoriesDAO {

    @Query("SELECT * FROM memories_table ORDER BY id ASC ")
    fun getAllPlaces():LiveData<List<Memory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: Memory)

    @Delete
    suspend fun deleteMemory(memory: Memory)
}