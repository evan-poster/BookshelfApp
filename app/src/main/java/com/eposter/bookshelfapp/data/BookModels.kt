package com.eposter.bookshelfapp.data

import android.util.Log
import com.eposter.bookshelfapp.data.local.BookEntity

private const val TAG = "BookModels"

data class GoogleBooksResponse(
    val kind: String? = null,
    val totalItems: Int = 0,
    val items: List<BookItem>? = null
)

data class BookItem(
    val id: String = "",
    val volumeInfo: VolumeInfo = VolumeInfo()
)

data class VolumeInfo(
    val title: String = "",
    val authors: List<String>? = null,
    val description: String? = null,
    val imageLinks: ImageLinks? = null
)

data class ImageLinks(
    val thumbnail: String? = null,
    val smallThumbnail: String? = null
)

// Domain model for UI
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val thumbnailUrl: String?,
    val description: String
)

// Extension function to convert API response to domain model
fun BookItem.toBook(): Book {
    Log.d(TAG, "Converting BookItem to Book: id=$id, title=${volumeInfo.title}")
    
    // Fix for http vs https in image URLs
    val thumbnailUrl = volumeInfo.imageLinks?.thumbnail
    val secureImageUrl = if (thumbnailUrl?.startsWith("http:") == true) {
        thumbnailUrl.replace("http:", "https:")
    } else {
        thumbnailUrl
    }
    
    Log.d(TAG, "Original thumbnail URL: $thumbnailUrl")
    Log.d(TAG, "Secure thumbnail URL: $secureImageUrl")
    
    return Book(
        id = id,
        title = volumeInfo.title.ifEmpty { "Unknown Title" },
        author = volumeInfo.authors?.joinToString(", ") ?: "Unknown Author",
        thumbnailUrl = secureImageUrl,
        description = volumeInfo.description ?: "No description available"
    )
}

// Extension function to convert domain model to entity
fun Book.toEntity(searchQuery: String): BookEntity {
    return BookEntity(
        id = id,
        title = title,
        author = author,
        thumbnailUrl = thumbnailUrl,
        description = description,
        searchQuery = searchQuery
    )
}