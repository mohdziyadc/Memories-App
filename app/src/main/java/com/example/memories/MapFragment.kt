package com.example.memories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import kotlinx.android.synthetic.main.fragment_map.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions



class MapFragment:Fragment(R.layout.fragment_map) {

    private var map:GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)


        mapView.getMapAsync {
            map = it

            val place =  LatLng(-8.60, 115.185);
            it.addMarker( MarkerOptions().position(place).title("Marker Title").snippet("Marker Description"));

            // For zooming automatically to the location of the marker
            val cameraPosition = CameraPosition.Builder().target(place).zoom(12f).build();
            it.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

    }

    override fun onDestroyView() { //**
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onStart() { //**
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() { //**
        super.onStop()
        mapView.onStop()
    }

    override fun onResume() { //**
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() { //**
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() { //**
        super.onLowMemory()
        mapView.onLowMemory()
    }

    //This function is used to cache the once loaded map
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}