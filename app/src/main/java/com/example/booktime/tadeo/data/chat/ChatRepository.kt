package com.example.booktime.tadeo.data.chat

import android.util.Log
import com.example.booktime.tadeo.data.model.ChatMessage
import com.google.firebase.firestore.FirebaseFirestore

class ChatRepository {

    private val db = FirebaseFirestore.getInstance()

    fun saveMessage(
        userId: String,
        bookId: String,
        message: ChatMessage
    ) {

        db.collection("users")
            .document(userId)
            .collection("chats")
            .document(bookId)
            .collection("messages")
            .add(message)
            .addOnFailureListener { e ->
                Log.e("ChatRepository", "Error al guardar el mensaje", e)
            }
    }

    fun loadMessages(
        userId: String,
        bookId: String,
        onResult: (List<ChatMessage>) -> Unit
    ) {

        db.collection("users")
            .document(userId)
            .collection("chats")
            .document(bookId)
            .collection("messages")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->

                val messages = result.documents.mapNotNull {
                    it.toObject(ChatMessage::class.java)
                }

                onResult(messages)
            }
            .addOnFailureListener { e ->
                Log.e("ChatRepository", "Error al cargar los mensajes", e)
                onResult(emptyList())
            }
    }
}