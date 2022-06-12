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
        // Context.LOCATION_SERVICE
        binding.txtTakeCamera.visibility = View.VISIBLE

        /*
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener
        {
            override fun onLocationChanged(location: Location) {
                // lokasyon,konum değişince yapılacaklar
                var adres=""
                val guncelKonum = LatLng(location.latitude,location.longitude)

                var geoCoder = Geocoder(view.context, Locale.getDefault())

                try {
                    val adresListesi = geoCoder.getFromLocation(guncelKonum.latitude,guncelKonum.longitude,1)
                    if(adresListesi.size >0)
                    {
                        adres +=adresListesi.get(0).countryName.toString()+"/"+adresListesi.get(0).adminArea.toString()+"/"+adresListesi.get(0).thoroughfare.toString() + "/"+ adresListesi.get(0).subThoroughfare.toString()
                        binding.txtLocation.text = adres
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
        }

        if(ContextCompat.checkSelfPermission(view.context,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // izin verilmedi izim istiyoruz
            ActivityCompat.requestPermissions(parentFragment!!.activity!!.parent,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }
        else
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,60,1f,locationListener)
        }
        */
        binding.addPhoto.setOnClickListener {
            fotoCek(view)
        }

        binding.btnSave.setOnClickListener {
            if(check()){
                addFavourite(view)
            }
        }

    }

    fun check() : Boolean{

        var placeName = binding.editTextPlaceName.text.toString().trim()

        if(placeName.isEmpty()){

            binding.editTextPlaceNameInputLayout.error = "Bu alan boş kalamaz!"
            return false
        }
        else{
            binding.editTextPlaceName.error = null
        }

        return true
    }

    fun fotoCek(view : View){

        gorselIsmi = UUID.randomUUID().toString()
        var storageDirectory = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {

            imageFile = File.createTempFile(gorselIsmi,".jpg",storageDirectory);

            gorselYolu = imageFile.absolutePath;
            gorselUrl = FileProvider.getUriForFile(view.context,
                "com.okankukul.favouriteplace.fileprovider",imageFile)

            if(ContextCompat.checkSelfPermission(view.context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                // izin alınmamış
                ActivityCompat.requestPermissions(activity!!.parent,arrayOf(Manifest.permission.CAMERA),111)
            }
            else
            {
                // izin alınmış zaten
                // galeriden fotoğraf alma kodu
                Toast.makeText(view.context,"ELSE GİRDİ", Toast.LENGTH_LONG).show()
                var cameraIntent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,gorselUrl)
                startActivityForResult(cameraIntent,101)

            }
        }
        catch (e : IOException){
            println(e.localizedMessage)
        }

    }

    fun addFavourite(view : View){
        val guncelKullanici = auth.currentUser

        Toast.makeText(view.context,"${gorselUrl}", Toast.LENGTH_LONG)
        if(gorselUrl != null){

            val reference = storage.reference
            val gorselReference =reference.child("images").child("${gorselIsmi}.jpg")

            gorselReference.putFile(gorselUrl!!).addOnSuccessListener { taskSnapshot ->
                var yuklenenGorselReference = FirebaseStorage.getInstance().reference.child("images").child("${gorselIsmi}.jpg")
                yuklenenGorselReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl =uri.toString()
                    val guncelKullaniciEmail = guncelKullanici!!.email.toString()
                    val guncelKullaniciName = guncelKullanici.displayName.toString()
                    val placeName = binding.editTextPlaceName.text.toString().trim()
                    val currentDate = LocalDateTime.now()
                    var adres = binding.txtLocation.text.toString()

                    // veri tabanı işlemleri
                    if(placeName != null){
                        val postHashMap = hashMapOf<String,Any>()
                        postHashMap.put("imageUrl",downloadUrl)
                        postHashMap.put("userEmail",guncelKullaniciEmail)
                        postHashMap.put("userName",guncelKullaniciName)
                        postHashMap.put("placeName",placeName)
                        postHashMap.put("date",currentDate)
                        postHashMap.put("location",adres)
                        fireStore.collection("Post").add(postHashMap).addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                Toast.makeText(view.context,"Mekan favorilere eklendi", Toast.LENGTH_LONG).show()

                            }

                        }.addOnFailureListener { exception ->
                            Toast.makeText(view.context,exception.localizedMessage, Toast.LENGTH_LONG).show()
                        }
                    }

                }.addOnFailureListener { exception ->
                    Toast.makeText(view.context,exception.localizedMessage, Toast.LENGTH_LONG).show()
                }

            }

        }
        else{
            Toast.makeText(view.context,"Fotoğraf Eklemediniz", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == 101 && resultCode == Activity.RESULT_OK && data != null){

            // gorselUrl = data.data

            binding.txtTakeCamera.visibility = View.GONE
            binding.imgCamera.scaleType = ImageView.ScaleType.CENTER_CROP
            gorselBitMap = BitmapFactory.decodeFile(gorselYolu) // data?.getParcelableExtra<Bitmap>("data")
            binding.imgCamera.setImageBitmap(gorselBitMap)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 111)
        {
            if(grantResults.size >0)
            {
                if(ContextCompat.checkSelfPermission(activity!!.applicationContext,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                {
                    var cameraIntent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,gorselUrl)
                    startActivityForResult(cameraIntent,101)
                }
            }
        }

        /*
        if(requestCode == 1){
            if(grantResults.size >0){
                if(ContextCompat.checkSelfPermission(activity!!.applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    // izni aldık konumu alıcaz
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,60,1f,locationListener)

                }
            }
        }*/
    }

}