package com.okankukul.favouriteplace.Home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.okankukul.favouriteplace.R
import com.okankukul.favouriteplace.databinding.ActivityAddBinding
import com.okankukul.favouriteplace.databinding.FragmentAddBinding
import com.okankukul.favouriteplace.databinding.FragmentHomeBinding
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.time.LocalDateTime
import java.util.*


class AddFragment(activity: Activity) : Fragment() {

    private lateinit var binding : FragmentAddBinding
    lateinit var auth : FirebaseAuth


    private lateinit var storage : FirebaseStorage
    private lateinit var fireStore : FirebaseFirestore

    private var gorselUrl : Uri? = null
    private var gorselBitMap : Bitmap? = null

    private lateinit var gorselYolu: String;
    private lateinit var gorselIsmi: String;
    private lateinit var imageFile: File;

    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener

    private lateinit var adres : String;
    private  lateinit var guncelKonum : LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        binding.txtTakeCamera.visibility = View.VISIBLE





    }


}