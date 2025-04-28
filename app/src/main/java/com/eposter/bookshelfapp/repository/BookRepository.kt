package com.eposter.bookshelfapp.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.eposter.bookshelfapp.data.Book
import com.eposter.bookshelfapp.data.BookItem
import com.eposter.bookshelfapp.data.toBook
import com.eposter.bookshelfapp.data.toEntity
import com.eposter.bookshelfapp.data.local.BookDatabase
import com.eposter.bookshelfapp.data.local.toBook
import com.eposter.bookshelfapp.network.BookApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

private const val TAG = "BookRepository"

class BookRepository(private val context: Context) {
    private val bookApiService = BookApi.service
    private val bookDao = BookDatabase.getDatabase(context).bookDao()
    
    suspend fun getBooks(query: String): List<Book> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Getting books for query: $query")
                if (isNetworkAvailable()) {
                    Log.d(TAG, "Network is available, fetching from API")
                    // Try to fetch from network
                    try {
                        val response = bookApiService.searchBooks(query)
                        Log.d(TAG, "API response received: ${response.totalItems} total items")
                        
                        if (response.items == null) {
                            Log.d(TAG, "API response items is null")
                            return@withContext emptyList<Book>()
                        }
                        
                        val books = response.items.map { 
                            Log.d(TAG, "Processing book: ${it.volumeInfo.title}")
                            Log.d(TAG, "Thumbnail URL: ${it.volumeInfo.imageLinks?.thumbnail}")
                            it.toBook() 
                        }
                        
                        Log.d(TAG, "Converted ${books.size} books from API response")
                        
                        // Cache the results
if (books.isNotEmpty()) {
    val existingBooks = bookDao.getBooksByQuery(query)  // Check if books exist
    if (existingBooks.isNotEmpty()) {
        // Optionally update instead of delete and insert, but for simplicity, delete only if exists
        bookDao.deleteBooksByQuery(query)
    }
    val bookEntities = books.map { it.toEntity(query) }
    bookDao.insertBooks(bookEntities)
    Log.d(TAG, "Cached ${bookEntities.size} books in database")
}
                        
                        books
                    } catch (e: IOException) {
                        // Network error, fall back to cache
                        Log.e(TAG, "Network error: ${e.message}", e)
                        loadFromCache(query)
                    } catch (e: Exception) {
                        // Other error, log and rethrow
                        Log.e(TAG, "Error fetching from API: ${e.message}", e)
                        throw e
                    }
                } else {
                    // No network, load from cache
                    Log.d(TAG, "Network is not available, loading from cache")
                    loadFromCache(query)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in getBooks: ${e.message}", e)
                loadFromCache(query)
            }
        }
    }
    
    private suspend fun loadFromCache(query: String): List<Book> {
        Log.d(TAG, "Loading books from cache for query: $query")
        val bookEntities = bookDao.getBooksByQuery(query)
        Log.d(TAG, "Found ${bookEntities.size} books in cache")
        return bookEntities.map { it.toBook() }
    }
    
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        Log.d(TAG, "Network available: $hasInternet")
        return hasInternet
    }
}
