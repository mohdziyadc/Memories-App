package com.example.memories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_new_place.*
import android.widget.DatePicker

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

import android.Manifest as Manifest

const val TAG = "NewPlaceFragment"

class NewPlaceFragment:Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_place, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        saveBtn.setOnClickListener {
            findNavController().navigate(R.id.action_newPlaceFragment_to_placesFragment)
        }
        toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_newPlaceFragment_to_placesFragment)
        }

        val myCalendar = Calendar.getInstance()

        val date =
            OnDateSetListener { _, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                updateLabel(myCalendar)
            }

        etDate.setOnClickListener {
            DatePickerDialog( requireContext(), date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH))
                .apply {
                    datePicker.maxDate = Date().time
                    show()
                }
        }

        addImgBtn.setOnClickListener {

            if(askForPermissions()){
                val items = arrayOf("Select an image from Gallery", "Open Camera")

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Select an Action")
                    .setItems(items) { _, which ->
                        when(which){
                            0 -> openGallery()
                            1 -> openCamera()
                        }
                    }
                    .show()

            }

        }


    }

    private fun askForPermissions():Boolean{
        var isPermissionGranted = false
        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(
                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                        if(p0!!.areAllPermissionsGranted()){
                            //launch an Intent
                            isPermissionGranted = true

                        }

                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        showPermissionDialog()
                        isPermissionGranted = true

                    }
                }
            ).check()
        return isPermissionGranted
    }

    private fun openCamera() {

    }

    private fun openGallery() {

    }

    private fun showPermissionDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Permissions Required")
            .setMessage("Gallery & Camera permissions are needed to perform this action.")
            .setPositiveButton("Go to Settings"){_,_ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", requireActivity().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: Exception){
                    Log.d(TAG, "onPermissionRationaleShouldBeShown: Permissions Denied")
                }

            }
            .setNegativeButton("Later"){dialog,_ ->

                dialog.dismiss()
            }
            .show()
    }

    private fun updateLabel(myCalendar:Calendar) {
        val myFormat = "MM/dd/yy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        etDate.setText(dateFormat.format(myCalendar.getTime()))
    }
}