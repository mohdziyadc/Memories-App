package com.example.memories.recyclerview

import android.content.Context
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.memories.PlacesFragmentDirections
import com.example.memories.R
import com.example.memories.room.Memory
import kotlinx.android.synthetic.main.fragment_place.view.*
import kotlinx.android.synthetic.main.list_item.view.*
import java.io.File

class MemoriesAdapter(): RecyclerView.Adapter<MemoriesAdapter.MemoriesViewHolder>() {

    inner class MemoriesViewHolder(itemView:View): RecyclerView.ViewHolder(itemView)
    private val differCallback = object : DiffUtil.ItemCallback<Memory>(){
        override fun areItemsTheSame(oldItem: Memory, newItem: Memory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Memory, newItem: Memory): Boolean {
            return oldItem.hashCode() == newItem.hashCode()      //basically checking if 2 memories are exactly equal.
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list:List<Memory>) = differ.submitList(list)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoriesViewHolder {
        return MemoriesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MemoriesViewHolder, position: Int) {
        val currentMemory = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this)
                .load(
                    currentMemory.image?.let {
                        File(it.path)
                    }
                )
                .circleCrop()
                .into(circle_image)

            placeTV.text = currentMemory.title
            dateTV.text = currentMemory.date

            setOnClickListener {
                val action = PlacesFragmentDirections.actionPlacesFragmentToPlaceFragment(currentMemory)
               findNavController().navigate(action)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}