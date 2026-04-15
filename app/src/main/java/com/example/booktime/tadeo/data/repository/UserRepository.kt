package com.example.booktime.tadeo.data.repository

import com.example.booktime.tadeo.data.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import android.net.Uri

class UserRepository {
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    fun uploadProfileImage(uri: Uri, onResult: (String?) -> Unit) {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            println(" UID NULL")
            onResult(null)
            return
        }

        val ref = storage.reference.child("profile_images/$uid.jpg")

        println("📤 SUBIENDO IMAGEN...")

        ref.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    println(" ERROR SUBIENDO: ${task.exception}")
                    throw task.exception!!
                }
                ref.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->

                println(" URL OBTENIDA: $downloadUrl")

                db.collection("users").document(uid)
                    .set(
                        mapOf("photoUrl" to downloadUrl.toString()),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
                    .addOnSuccessListener {
                        println("GUARDADO EN FIRESTORE")
                        onResult(downloadUrl.toString())
                    }
                    .addOnFailureListener {
                        println(" ERROR FIRESTORE: ${it.message}")
                        onResult(null)
                    }
            }
            .addOnFailureListener {
                println(" ERROR GENERAL: ${it.message}")
                onResult(null)
            }
    }
    fun getUser(onResult: (User?) -> Unit) {
        val firebaseUser = auth.currentUser

        if (firebaseUser == null) {
            onResult(null)
            return
        }

        val uid = firebaseUser.uid

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->

                val user = document.toObject(User::class.java)


                if (user != null) {
                    onResult(user)
                } else {
                    onResult(
                        User(
                            uid = uid,
                            name = firebaseUser.displayName ?: "Usuario",
                            email = firebaseUser.email ?: ""
                        )
                    )
                }
            }
            .addOnFailureListener {
                onResult(
                    User(
                        uid = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "Usuario",
                        email = firebaseUser.email ?: ""
                    )
                )
            }
    }

    fun updateUser(name: String, email: String, onResult: (Boolean) -> Unit) {
        val user = auth.currentUser ?: return
        val uid = user.uid

        val updates = mapOf(
            "name" to name,
            "email" to email
        )

        db.collection("users").document(uid)
            .update(updates)
            .addOnSuccessListener {
                user.updateEmail(email)
                    .addOnSuccessListener { onResult(true) }
                    .addOnFailureListener { onResult(false) }
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val user = auth.currentUser
        val email = user?.email

        if (user == null || email == null) {
            onResult(false, "Usuario no autenticado")
            return
        }

        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        onResult(true, "Contraseña actualizada correctamente")
                    }
                    .addOnFailureListener {
                        onResult(false, "Error al actualizar contraseña")
                    }
            }
            .addOnFailureListener {
                onResult(false, "Contraseña actual incorrecta")
            }
    }

    fun logout() {
        auth.signOut()
    }
}