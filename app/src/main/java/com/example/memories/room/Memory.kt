package com.example.memories.room

import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "memories_table", indices = [Index(value = ["title", "date", "image"], unique = true)])
@Parcelize
data class Memory(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    @ColumnInfo(name = "title")
    val title:String,
    val desc:String,
    @ColumnInfo(name = "image")
    var image: Uri? = null,
    @ColumnInfo(name = "date")
    val date:String,
    val location:String,
    val latitude:Long = 0L,
    val longitude:Long = 0L
): Parcelable

