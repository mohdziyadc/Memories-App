package com.example.memories.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@TypeConverters(Converters::class)
@Database(entities = [Memory::class], version = 1)
abstract class MemoriesDB : RoomDatabase() {


    abstract fun getMemoriesDAO():MemoriesDAO


    companion object{
        private var INSTANCE:MemoriesDB? = null

        fun getDatabase(context: Context):MemoriesDB{

            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MemoriesDB::class.java,
                    "memories_database"
                ).build()
                INSTANCE = instance
                return instance

            }
        }



    }
}