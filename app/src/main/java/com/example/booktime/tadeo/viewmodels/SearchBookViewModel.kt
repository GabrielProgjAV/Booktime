package com.example.booktime.tadeo.viewmodels

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import android.app.Application
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.booktime.tadeo.data.model.Book
import com.example.booktime.tadeo.data.model.GoogleBookItem
import com.example.booktime.tadeo.data.repository.GoogleBooksRepository
import com.example.booktime.tadeo.data.repository.BookRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.BuildConfig

class SearchBookViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GoogleBooksRepository()
    private val bookRepository = BookRepository()
    private val auth = FirebaseAuth.getInstance()
    
    private fun getPrefs(): android.content.SharedPreferences {
        val uid = auth.currentUser?.uid ?: "guest"
        return getApplication<Application>().getSharedPreferences("booktime_settings_$uid", Context.MODE_PRIVATE)
    }

    var query by mutableStateOf("")
    var results by mutableStateOf<List<GoogleBookItem>>(emptyList())
    var isLoading by mutableStateOf(false)
    var isSaving by mutableStateOf(false)
    var selectedBookId by mutableStateOf<String?>(null)
    var saveError by mutableStateOf<String?>(null)

    fun onSearch() {
        if (query.isBlank()) return
        viewModelScope.launch {
            isLoading = true
            selectedBookId = null
            try {
                val response = repository.search(query, BuildConfig.BOOKS_API_KEY)
                results = response.items ?: emptyList()
            } catch (e: Exception) {
                results = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    fun selectBook(id: String) { selectedBookId = id }

    fun saveBookToFirebase(userId: String, onSuccess: (Book) -> Unit) {
        val selectedItem = results.find { it.id == selectedBookId } ?: return

        viewModelScope.launch {
            isSaving = true
            saveError = null
            val newBook = Book(
                id = selectedItem.id,
                title = selectedItem.volumeInfo.title,
                author = selectedItem.volumeInfo.authors?.joinToString() ?: "Desconocido",
                imageUrl = selectedItem.volumeInfo.imageLinks?.httpsThumbnail ?: "",
                addedAt = Timestamp.now(),
                progress = 0,

                fileUri =
                    selectedItem.accessInfo?.webReaderLink
                        ?: selectedItem.volumeInfo.previewLink
                        ?: "",

                description =
                    selectedItem.volumeInfo.description
                        ?: "Sin descripción disponible"
            )
            try {
                val saved = repository.saveBookToFirebase(getApplication(), userId, newBook)
                if (saved) {
                    onSuccess(newBook)
                } else {
                    saveError = "No se pudo guardar el libro. Inténtalo de nuevo."
                    Log.e("SAVE_ERROR", "saveBookToFirebase devolvió false")
                }
            } catch (e: Exception) {
                saveError = "No se pudo guardar el libro: ${e.message}"
                Log.e("SAVE_ERROR", e.message.toString())
            } finally {
                isSaving = false
            }
        }
    }
}
