package com.example.placebook.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.placebook.model.Bookmark
import com.example.placebook.repository.BookmarkRepo
import com.example.placebook.util.ImageUtils
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place

class MapsViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "MapsViewModel"

    private val bookmarkRepo = BookmarkRepo(application)

    private var bookmarks: LiveData<List<BookmarkMarkerView>>? = null

    fun addBookmarkFromPlace(place: Place, image: Bitmap?) {
        val bookmark = bookmarkRepo.createBookmark()
        bookmark.placeId = place.id
        bookmark.address = place.address.toString()
        bookmark.latitude = place.latLng?.latitude ?: 0.0
        bookmark.longitude = place.latLng?.longitude ?: 0.0
        bookmark.name = place.name.toString()
        bookmark.phone = place.phoneNumber.toString()

        val newId = bookmarkRepo.addBookmark(bookmark)
        image?.let {
            bookmark.setImage(it, getApplication())
        }
        Log.i(TAG, "New Bookmark $newId added to the database.")
    }

    // This class to add a blue mark after the store is placed into DataBase
    data class BookmarkMarkerView(
        var id: Long? = null,
        var location: LatLng = LatLng(0.0, 0.0)
    ){
        fun getImage(context: Context) = id?.let {
            ImageUtils.loadBitmapFromFile(context, Bookmark.generateImageFileName(it))
        }
    }

    private fun bookmarkToMarkerView(bookmark: Bookmark) =
        BookmarkMarkerView(bookmark.id, LatLng(bookmark.latitude, bookmark.longitude))


    // This is a helper method that converts a Bookmark object from the repo into a BookmarkMarkerView object
    private fun mapBookmarksToMarkerView() {
        bookmarks = Transformations.map(bookmarkRepo.allBookmarks){
            it.map {
                bookmarkToMarkerView(it)
            }
        }
    }

    fun getBookmarkMarkerView(): LiveData<List<BookmarkMarkerView>>? {
        if (bookmarks == null){
            mapBookmarksToMarkerView()
        }
        return bookmarks
    }
}