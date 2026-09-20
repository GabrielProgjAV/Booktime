package com.example.booktime.tadeo.data.repository

import android.content.Context
import com.example.booktime.tadeo.data.model.Book
import com.example.booktime.tadeo.data.model.GoogleBooksResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query as ApiQuery

interface GoogleBooksApi {
    @GET("volumes")
    suspend fun searchBooks(
        @ApiQuery("q") query: String,
        @ApiQuery("key") apiKey: String,
        @ApiQuery("maxResults") maxResults: Int = 20
    ): GoogleBooksResponse
}

class GoogleBooksRepository {

    private val firestore = FirebaseFirestore.getInstance()

    private val api = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/books/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GoogleBooksApi::class.java)

    suspend fun search(query: String, apiKey: String) =
        api.searchBooks(query, apiKey)

    suspend fun saveBookToFirebase(context: Context, userId: String, book: Book): Boolean {
        return try {
            firestore.collection("users").document(userId)
                .collection("library").document(book.id)
                .set(book).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getUserLibrary(context: Context, userId: String): List<Book> {
        return try {
            val snapshot = firestore.collection("users").document(userId)
                .collection("library")
                .get().await()
            
            snapshot.toObjects(Book::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

