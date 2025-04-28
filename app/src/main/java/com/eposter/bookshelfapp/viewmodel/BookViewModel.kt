package com.eposter.bookshelfapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eposter.bookshelfapp.data.Book
import com.eposter.bookshelfapp.repository.BookRepository
import kotlinx.coroutines.launch

private const val TAG = "BookViewModel"

class BookViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BookRepository(application.applicationContext)
    
    var books by mutableStateOf<List<Book>>(emptyList())
        private set
        
    var isLoading by mutableStateOf(false)
        private set
        
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var isOfflineMode by mutableStateOf(false)
        private set
    
    init {
        // Load default books on initialization
        Log.d(TAG, "Initializing BookViewModel, loading default books")
        loadBooks("programming")
    }
    
    fun loadBooks(query: String) {
        // Format the query to replace spaces with plus signs
        val formattedQuery = query.trim().replace("\\s+".toRegex(), "+")
        Log.d(TAG, "Loading books for query: '$query' (formatted: '$formattedQuery')")
        
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            isOfflineMode = false
            
            try {
                Log.d(TAG, "Calling repository.getBooks()")
                books = repository.getBooks(formattedQuery)
                Log.d(TAG, "Repository returned ${books.size} books")
                
                if (books.isEmpty()) {
                    Log.d(TAG, "No books found for query")
                    errorMessage = "No books found for '$query'"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading books: ${e.message}", e)
                errorMessage = "Failed to load books: ${e.message}"
                books = emptyList()
                
                // Try to load from cache as a fallback
                try {
                    Log.d(TAG, "Trying to load from cache as fallback")
                    val cachedBooks = repository.getBooks(formattedQuery)
                    if (cachedBooks.isNotEmpty()) {
                        Log.d(TAG, "Found ${cachedBooks.size} books in cache")
                        books = cachedBooks
                        isOfflineMode = true
                        errorMessage = "Showing cached results. ${e.message}"
                    } else {
                        Log.d(TAG, "No books found in cache")
                    }
                } catch (cacheException: Exception) {
                    // If cache also fails, keep the original error message
                    Log.e(TAG, "Cache fallback also failed: ${cacheException.message}", cacheException)
                }
            } finally {
                isLoading = false
                Log.d(TAG, "Loading completed. Books: ${books.size}, Error: $errorMessage, Offline: $isOfflineMode")
            }
        }
    }
}