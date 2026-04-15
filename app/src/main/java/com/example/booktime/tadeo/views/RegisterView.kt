package com.example.booktime.tadeo.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
@Composable
fun RegisterScreen(onBackClick: () -> Unit, onRegisterSuccess: () -> Unit) {
    val auth = FirebaseAuth.getInstance()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    if (showErrorDialog) {
        AnimatedDialog(
            title = "Error de validación",
            text = errorMessage,
            onDismiss = { showErrorDialog = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4A5A6E))
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(top = 48.dp, start = 16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Nombre completo", style = MaterialTheme.typography.bodyLarge) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFD9D9D9),
                    unfocusedContainerColor = Color(0xFFD9D9D9),
                    focusedIndicatorColor = Color(0xFF54C35D),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                ),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Correo electrónico", style = MaterialTheme.typography.bodyLarge) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFD9D9D9),
                    unfocusedContainerColor = Color(0xFFD9D9D9),
                    focusedIndicatorColor = Color(0xFF54C35D),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                ),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Contraseña", style = MaterialTheme.typography.bodyLarge) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFD9D9D9),
                    unfocusedContainerColor = Color(0xFFD9D9D9),
                    focusedIndicatorColor = Color(0xFF54C35D),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                ),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = { Text("Confirmar contraseña", style = MaterialTheme.typography.bodyLarge) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFD9D9D9),
                    unfocusedContainerColor = Color(0xFFD9D9D9),
                    focusedIndicatorColor = Color(0xFF54C35D),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Button(
                onClick = {
                    if (isLoading) return@Button
                    if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                        errorMessage = "Por favor, completa todos los espacios para continuar."
                        showErrorDialog = true
                    } else if (password.length < 8 || password.length > 16) {
                        errorMessage = "La contraseña debe tener entre 8 y 16 caracteres."
                        showErrorDialog = true
                    } else if (password != confirmPassword) {
                        errorMessage = "Las contraseñas no coinciden."
                        showErrorDialog = true
                    } else {
                        isLoading = true

                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {

                                    val user = auth.currentUser
                                    val uid = user?.uid ?: ""

                                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build()

                                    user?.updateProfile(profileUpdates)

                                    //Guarda en firestore
                                    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

                                    val userMap = mapOf(
                                        "uid" to uid,
                                        "name" to name,
                                        "email" to email
                                    )

                                    db.collection("users").document(uid)
                                        .set(userMap)
                                        .addOnSuccessListener {
                                            println("USUARIO GUARDADO EN FIRESTORE")
                                            onRegisterSuccess()
                                        }
                                        .addOnFailureListener {
                                            errorMessage = "Error guardando usuario en base de datos"
                                            showErrorDialog = true
                                        }

                                } else {
                                    val exception = task.exception

                                    errorMessage = when (exception) {
                                        is com.google.firebase.auth.FirebaseAuthUserCollisionException ->
                                            "Este correo ya está registrado."
                                        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ->
                                            "Correo inválido."
                                        else -> exception?.message ?: "Error desconocido"
                                    }
                                    showErrorDialog = true
                                }
                            }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF54C35D),
                    contentColor = Color.White
                )
            ) {


                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Registrarse",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}