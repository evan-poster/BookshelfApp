package com.eposter.bookshelfapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eposter.bookshelfapp.data.Book
import com.eposter.bookshelfapp.repository.BookRepository
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {
	private val repository = BookRepository()
	
	var books by mutableStateOf<List<Book>>(emptyList())
		private set
		
	var isLoading by mutableStateOf(false)
		private set
		
	var errorMessage by mutableStateOf<String?>(null)
		private set
	
	init {
		loadBooks()
	}
	
	fun loadBooks(query: String = "jazz+history") {
		viewModelScope.launch {
			isLoading = true
			errorMessage = null
			try {
				books = repository.getBooks(query)
			} catch (e: Exception) {
				errorMessage = "Failed to load books: ${e.message}"
			} finally {
				isLoading = false
			}
		}
	}
}