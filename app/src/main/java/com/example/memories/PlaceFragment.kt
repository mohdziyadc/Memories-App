package com.example.memories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.memories.viewmodel.MemoriesViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_new_place.*
import kotlinx.android.synthetic.main.fragment_place.*
import kotlinx.android.synthetic.main.fragment_place.toolbar
import java.io.File

class PlaceFragment:Fragment(){

    private val args by navArgs<PlaceFragmentArgs>()

    private val viewModel:MemoriesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        memoryTitleTv.text = args.currentMemory.title
        toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_placeFragment_to_placesFragment)
        }
        Glide.with(requireContext())
            .load(
                args.currentMemory.image?.let {
                        File(it.path)
                }
            )
            .centerCrop()
            .placeholder(R.drawable.ic_baseline_image_24)
            .into(placeIV)

        place_desc.text = args.currentMemory.desc

        deleteBtn.setOnClickListener {
            showAlertDialog()
            Log.d(TAG, "onViewCreated: Button Clicked")

        }

        mapBtn.setOnClickListener {
            findNavController().navigate(R.id.action_placeFragment_to_mapFragment)
        }
    }

    private fun showAlertDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Memory")
            .setMessage("Are you sure? This will delete the memory.")
            .setPositiveButton("Yes"){ _,_ ->
                deleteMemory()
            }
            .setNegativeButton("No"){dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteMemory() {
        viewModel.deleteMemory(args.currentMemory)
        findNavController().navigate(R.id.action_placeFragment_to_placesFragment)
    }
}