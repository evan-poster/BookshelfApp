package com.eposter.bookshelfapp.data

data class GoogleBooksResponse(
    val items: List<BookItem>? = null,
    val totalItems: Int = 0
)

data class BookItem(
    val id: String = "",
    val volumeInfo: VolumeInfo = VolumeInfo()
)

data class VolumeInfo(
    val title: String = "",
    val authors: List<String>? = null,
    val description: String? = null,
    val imageLinks: ImageLinks? = null,
    val publishedDate: String? = null
)

data class ImageLinks(
    val smallThumbnail: String? = null,
    val thumbnail: String? = null
)

// Simplified Book model for UI
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val thumbnailUrl: String?,
    val description: String
)

// Extension function to convert from API model to UI model
fun BookItem.toBook(): Book {
    return Book(
        id = id,
        title = volumeInfo.title.ifEmpty { "Unknown Title" },
        author = volumeInfo.authors?.joinToString(", ") ?: "Unknown Author",
        thumbnailUrl = volumeInfo.imageLinks?.thumbnail?.replace("http:", "https:"),
        description = volumeInfo.description ?: "No description available"
    )
}