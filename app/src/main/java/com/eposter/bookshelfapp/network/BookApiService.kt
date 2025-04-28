package com.eposter.bookshelfapp.network

import com.eposter.bookshelfapp.data.GoogleBooksResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://www.googleapis.com/books/v1/"

interface BookApiService {
	@GET("volumes")
	suspend fun searchBooks(@Query("q") query: String): GoogleBooksResponse
}

object BookApi {
	private val retrofit = Retrofit.Builder()
		.baseUrl(BASE_URL)
		.addConverterFactory(GsonConverterFactory.create())
		.build()

	val service: BookApiService = retrofit.create(BookApiService::class.java)
}