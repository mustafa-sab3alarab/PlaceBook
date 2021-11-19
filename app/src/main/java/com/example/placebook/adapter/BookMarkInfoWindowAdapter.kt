package com.example.placebook.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import com.example.placebook.databinding.ContentBookmarkInfoBinding
import com.example.placebook.ui.MapsActivity
import com.example.placebook.util.ImageUtils
import com.example.placebook.viewmodel.MapsViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class BookMarkInfoWindowAdapter(val context: Activity) : GoogleMap.InfoWindowAdapter {

    private val binding = ContentBookmarkInfoBinding.inflate(context.layoutInflater)

    // Customize Window
    override fun getInfoWindow(p0: Marker): View? {
        return null
    }

    // Default Window
    override fun getInfoContents(marker: Marker): View? {
        binding.title.text = marker.title ?: ""
        binding.phone.text = marker.snippet ?: ""


//        binding.notes.text =
//
        val imageView = binding.photo
        when (marker.tag) {
            is MapsActivity.PlaceInfo -> {
                imageView.setImageBitmap((marker.tag as MapsActivity.PlaceInfo).image)
            }
            is MapsViewModel.BookmarkMarkerView -> {
                val bookMarkView = marker.tag as MapsViewModel.BookmarkMarkerView
                // Set imageView bitmap here
                imageView.setImageBitmap(bookMarkView.getImage(context))
            }
        }


        return binding.root
    }


}