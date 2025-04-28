package com.eposter.bookshelfapp.repository

import com.eposter.bookshelfapp.data.Book
import com.eposter.bookshelfapp.data.toBook
import com.eposter.bookshelfapp.network.BookApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookRepository {
	private val bookApiService = BookApi.service
	
	suspend fun getBooks(query: String): List<Book> {
		return withContext(Dispatchers.IO) {
			try {
				val response = bookApiService.searchBooks(query)
				if (response.items.isNullOrEmpty()) {
					return@withContext emptyList<Book>()
				}
				response.items.map { it.toBook() }
			} catch (e: Exception) {
				// In a real app, you'd want better error handling
				e.printStackTrace()
				emptyList()
			}
		}
	}
}