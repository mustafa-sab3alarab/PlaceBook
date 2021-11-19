package com.example.placebook.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.placebook.db.PlaceBookDatabase
import com.example.placebook.model.Bookmark

class BookmarkRepo(context: Context) {

    private val db = PlaceBookDatabase.getInstance(context)


    fun addBookmark(bookmark: Bookmark): Long {
        val newId = db.bookmarkDao().insertBookmark(bookmark)
        bookmark.id = newId
        return newId
    }


    fun createBookmark(): Bookmark {
        return Bookmark()
    }


    val allBookmarks: LiveData<List<Bookmark>>
        get() {
            return db.bookmarkDao().loadAll()
        }

    fun getLiveBookmark(bookmarkId: Long): LiveData<Bookmark> =
        db.bookmarkDao().loadLiveBookmark(bookmarkId)


    fun updateBookmark(bookmark: Bookmark) {
        db.bookmarkDao().updateBookmark(bookmark)
    }

    fun getBookmark(bookmarkId: Long): Bookmark {
        return db.bookmarkDao().loadBookmark(bookmarkId)
    }
}