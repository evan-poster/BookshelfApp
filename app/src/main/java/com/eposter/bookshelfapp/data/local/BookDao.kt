package com.eposter.bookshelfapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookDao {
	@Query("SELECT * FROM books WHERE searchQuery = :query ORDER BY timestamp DESC")
	suspend fun getBooksByQuery(query: String): List<BookEntity>
	
	@Query("SELECT * FROM books ORDER BY timestamp DESC LIMIT 20")
	suspend fun getRecentBooks(): List<BookEntity>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertBooks(books: List<BookEntity>)
	
	@Query("DELETE FROM books WHERE searchQuery = :query")
	suspend fun deleteBooksByQuery(query: String)
}