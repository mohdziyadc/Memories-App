package com.example.memories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memories.recyclerview.MemoriesAdapter
import com.example.memories.viewmodel.MemoriesViewModel
import kotlinx.android.synthetic.main.fragment_places.*

class PlacesFragment:Fragment() {

    private val viewModel:MemoriesViewModel by viewModels()

    private lateinit var memoriesAdapter: MemoriesAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_places, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_placesFragment_to_newPlaceFragment)
        }

        memoriesAdapter = MemoriesAdapter()
        setupRecyclerView()

        viewModel.memoriesList.observe(viewLifecycleOwner, {
            memoriesAdapter.submitList(it)
        })
    }

    private fun setupRecyclerView() {
        placesRV.apply {
            adapter = memoriesAdapter
            layoutManager = LinearLayoutManager(requireContext())


        }
    }
}