package com.example.memories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_new_place.*


import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

import android.Manifest as Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.memories.room.Memory
import com.example.memories.viewmodel.MemoriesViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


const val TAG = "NewPlaceFragment"
const val CAMERA_REQUEST = 1
const val IMAGE_DIR = "MemoriesDirectory"
const val PLACES_REQUEST = 2

class NewPlaceFragment:Fragment() {


    private val viewModel:MemoriesViewModel by viewModels()

    var mLatitude:Long? = 0L
    var mLongitude:Long? = 0L


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_place, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_newPlaceFragment_to_placesFragment)
        }

        if(!Places.isInitialized()){
            Places.initialize(requireActivity().applicationContext, resources.getString(R.string.google_maps_API_KEY))
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

        etLocation.setOnClickListener {
            setupPlacesActivity()
        }



        saveBtn.setOnClickListener {
            findNavController().navigate(R.id.action_newPlaceFragment_to_placesFragment)
            insertMemory()
        }


    }

    private fun setupPlacesActivity() {
        val fields = listOf(
            Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS
        )

        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(requireActivity())

        startActivityForResult(intent, PLACES_REQUEST)
    }

    private fun insertMemory(){
        val memory = Memory(
            id = 0,
            title = etTitle.text.toString(),
            desc = etDesc.text.toString(),
            date = etDate.text.toString(),
            location = etLocation.text.toString(),
            image = viewModel.imageUri,
        )

        viewModel.insertMemory(memory)
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
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(cameraIntent.resolveActivity(requireActivity().packageManager)!= null){
            startActivityForResult(cameraIntent, CAMERA_REQUEST)
        }
    }


    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        if(galleryIntent.resolveActivity(requireActivity().packageManager) != null){

            startActivityForResult(Intent.createChooser(galleryIntent, "Select an image"), 0)

        }
    }

    private fun saveImageToInternalStorage(bmp:Bitmap):Uri{

        val wrapper = ContextWrapper(requireContext())
        val directory = wrapper.getDir(IMAGE_DIR, Context.MODE_PRIVATE)
        val file = File(directory, "${UUID.randomUUID()}.jpg")

        try{
            val stream:OutputStream = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()

        }catch (e:IOException){
            e.printStackTrace()
        }

        return Uri.parse(file.absolutePath)
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
        val date = dateFormat.format(myCalendar.getTime())
        etDate.setText(date)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode == RESULT_OK){
            val uri = data?.data
            Glide.with(requireContext())
                .load(uri)
                .centerCrop()
                .into(placeImg)
            val bmp = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
            viewModel.imageUri = saveImageToInternalStorage(bmp)
            Log.d(TAG, "onActivityResult: ${viewModel.imageUri}")


        } else
        if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            val bmp = data?.extras?.get("data") as Bitmap
            Glide.with(requireContext())
                .load(bmp)
                .centerCrop()
                .into(placeImg)

            viewModel.imageUri = saveImageToInternalStorage(bmp)
            Log.d(TAG, "onActivityResult: ${viewModel.imageUri}")



        } else if(requestCode == PLACES_REQUEST && resultCode == RESULT_OK){
            val place:Place = Autocomplete.getPlaceFromIntent(data!!)
            etLocation.setText(place.address)
            mLatitude = place.latLng?.latitude?.toLong()
            mLongitude = place.latLng?.longitude?.toLong()
        }
    }
}