package com.eposter.bookshelfapp.data

data class GoogleBooksResponse(
	val items: List<BookItem> = emptyList(),
	val totalItems: Int = 0
)

data class BookItem(
	val id: String = "",
	val volumeInfo: VolumeInfo = VolumeInfo()
)

data class VolumeInfo(
	val title: String = "",
	val authors: List<String> = emptyList(),
	val description: String = "",
	val imageLinks: ImageLinks? = null,
	val publishedDate: String = ""
)

data class ImageLinks(
	val smallThumbnail: String = "",
	val thumbnail: String = ""
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
		title = volumeInfo.title,
		author = volumeInfo.authors.joinToString(", ") { it },
		thumbnailUrl = volumeInfo.imageLinks?.thumbnail?.replace("http:", "https:"),
		description = volumeInfo.description
	)
}