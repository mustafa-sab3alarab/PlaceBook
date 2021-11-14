package com.example.placebook.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.placebook.model.Bookmark
import com.example.placebook.repository.BookmarkRepo
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place

class MapsViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "MapsViewModel"

    private val bookmarkRepo = BookmarkRepo(application)

    fun addBookmarkFromPlace(place: Place, image: Bitmap?) {
        val bookmark = bookmarkRepo.createBookmark()
        bookmark.placeId = place.id
        bookmark.address = place.address.toString()
        bookmark.latitude = place.latLng?.latitude ?: 0.0
        bookmark.longitude = place.latLng?.longitude ?: 0.0
        bookmark.name = place.name.toString()
        bookmark.phone = place.phoneNumber.toString()

        val newId = bookmarkRepo.addBookmark(bookmark)
        Log.i(TAG, "New Bookmark $newId added to the database.")
    }

    // This class to add blue mark after store place to DataBase
    data class BookmarkMarkerView(
        var id:Long? = null,
        val location :LatLng = LatLng(0.0,0.0)
    )

}