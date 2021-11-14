package com.example.placebook.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.placebook.model.Bookmark

@Dao
interface BookmarkDao {

    @Insert
    fun insertBookmark(bookmark: Bookmark): Long

    @Update
    fun updateBookmark(bookmark: Bookmark)

    @Delete
    fun deleteBookmark(bookmark: Bookmark)

    @Query("SELECT * FROM book_mark")
    fun loadAll(): LiveData<List<Bookmark>>

    @Query("SELECT * FROM book_mark WHERE id = :bookmarkId")
    fun loadBookmark(bookmarkId: Long): Bookmark

    @Query("SELECT * FROM book_mark WHERE id = :bookmarkId")
    fun loadLiveBookmark(bookmarkId: Long): LiveData<Bookmark>

}