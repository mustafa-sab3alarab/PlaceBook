package com.example.placebook.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import com.example.placebook.databinding.ContentBookmarkInfoBinding
import com.example.placebook.ui.MapsActivity
import com.example.placebook.viewmodel.MapsViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class BookMarkInfoWindowAdapter(context: Activity) : GoogleMap.InfoWindowAdapter {

    private val binding = ContentBookmarkInfoBinding.inflate(context.layoutInflater)

    // Customize Window
    override fun getInfoWindow(p0: Marker): View? {
        return null
    }

    // Default Window
    override fun getInfoContents(marker: Marker): View? {
        binding.title.text = marker.title ?: ""
        binding.phone.text = marker.snippet ?: ""
        val imageView = binding.photo

        when(marker.tag){
            is MapsActivity.PlaceInfo -> {
                imageView.setImageBitmap((marker.tag as MapsActivity.PlaceInfo).image)
            }
        }
        return binding.root
    }


}