package com.eposter.bookshelfapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.eposter.bookshelfapp.data.Book

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val thumbnailUrl: String?,
    val description: String,
    val searchQuery: String, // To associate with a specific search query
    val timestamp: Long = System.currentTimeMillis() // Add timestamp with default value
)

// Extension function to convert entity to domain model
fun BookEntity.toBook(): Book {
    return Book(
        id = id,
        title = title,
        author = author,
        thumbnailUrl = thumbnailUrl,
        description = description
    )
}

fun Book.toEntity(searchQuery: String): BookEntity {
    return BookEntity(
        id = id,
        title = title,
        author = author,
        thumbnailUrl = thumbnailUrl,
        description = description,
        searchQuery = searchQuery,
        timestamp = System.currentTimeMillis() // Set current timestamp
    )
}