package com.example.placebook.ui

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.placebook.R
import com.example.placebook.adapter.BookMarkInfoWindowAdapter
import com.example.placebook.databinding.ActivityMapsBinding
import com.example.placebook.viewmodel.MapsViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val mapsViewModel by viewModels<MapsViewModel>()

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding


    // Returns the best most recent location currently available.
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient


    private lateinit var placesClient: PlacesClient


    companion object {
        private const val TAG = "MapsActivity"
        private const val REQUEST_LOCATION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // binding layout with UI
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupLocationClient()

        setupPlacesClient()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setupMapListeners()
        getCurrentLocation()
    }

    private fun setupLocationClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    /*
    * get Current User Location after checked to the request permissions
    * by isMyLocationEnabled variable
    */
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions()
        } else {

            mMap.isMyLocationEnabled = true
            fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                val location = it.result
                if (location != null) {
                    val latlng = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16.0f))
                } else {
                    Log.e(TAG, "no location found")
                }
            }
        }
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            }
        }
    }




    private fun setupMapListeners(){
        mMap.setInfoWindowAdapter(BookMarkInfoWindowAdapter(this))
        mMap.setOnPoiClickListener {
            displayPOI(it)
        }

        // if click in info window -> while we need to save in DataBase
        mMap.setOnInfoWindowClickListener {
            handleInfoWindowClick(it)
        }
    }

    private fun handleInfoWindowClick(marker: Marker) {
        val placeInfo = (marker.tag as PlaceInfo)
        if (placeInfo.place != null) {
            GlobalScope.launch {
                mapsViewModel.addBookmarkFromPlace(placeInfo.place, placeInfo.image)
            }
        }
        marker.remove()
    }

    private fun setupPlacesClient(){
        Places.initialize(applicationContext,getString(R.string.google_maps_key))
        placesClient = Places.createClient(this)
    }

    // Initialize Point Of Interest
    private fun displayPOI(pointOfInterest: PointOfInterest){
        displayPoiGetPlaceStep(pointOfInterest)
    }

    // Get Place to Marker Point of Interest
    private fun displayPoiGetPlaceStep(pointOfInterest: PointOfInterest) {
        val placeId = pointOfInterest.placeId

        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.PHONE_NUMBER,
            Place.Field.PHOTO_METADATAS,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )


        val request = FetchPlaceRequest
            .builder(placeId, placeFields)
            .build()

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val place = response.place
                displayPoiGetPhotoStep(place)
            }
            .addOnFailureListener { exception ->
                if (exception is ApiException) {
                    val statusCode = exception.statusCode
                    Log.e(TAG, "Place not found: " + exception.message + ", " + "statusCode: " + statusCode)
                }
            }
    }

    // Get Photo to Marker Point of Interest
    private fun displayPoiGetPhotoStep(place: Place) {
        val photoMetadata = place.photoMetadatas[1] ?: return

        val photoRequest = FetchPhotoRequest
            .builder(photoMetadata)
            .setMaxWidth(resources.getDimensionPixelSize(R.dimen.default_image_width))
            .setMaxHeight(resources.getDimensionPixelSize(R.dimen.default_image_height))
            .build()

        placesClient.fetchPhoto(photoRequest)
            .addOnSuccessListener { fetchPhotoResponse ->
            val bitmap = fetchPhotoResponse.bitmap
            // set image to image view
            displayPoiDisplayStep(place,bitmap)

        }.addOnFailureListener { exception ->
            if (exception is ApiException) {
                val statusCode = exception.statusCode
                Log.e(TAG, "Place not found: " + exception.message + ", " + "statusCode: " + statusCode)
            }
        }
    }

    // Marker Point of Interest
    private fun displayPoiDisplayStep(place: Place, photo: Bitmap?) {

        val marker = mMap.addMarker(
            MarkerOptions()
                .position(place.latLng as LatLng)
                .title(place.name)
                .snippet(place.phoneNumber)
        )

        marker?.tag = PlaceInfo(place, photo)
        marker?.showInfoWindow()
    }


    class PlaceInfo(val place: Place? = null, val image: Bitmap? = null)
}
