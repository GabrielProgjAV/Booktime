package com.example.booktime.tadeo.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.components.BooktimeButton
import com.example.booktime.tadeo.components.BooktimeTextField
import com.example.booktime.tadeo.components.ScreenWrapper
import com.example.booktime.tadeo.utils.ValidationUtils

@Composable
fun LoginScreen(onBackClick: () -> Unit, onForgotPasswordClick: () -> Unit, onLoginSuccess: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val errorEmptyFields = stringResource(id = R.string.error_empty_fields)
    val errorInvalidEmail = stringResource(id = R.string.error_invalid_email)
    val errorTitle = stringResource(id = R.string.error_title)
    val errorUserNotFound = stringResource(id = R.string.error_user_not_found)
    val errorWrongPassword = stringResource(id = R.string.error_wrong_password)
    val errorLoginGeneric = stringResource(id = R.string.error_login_generic)

    if (showErrorDialog) {
        AnimatedDialog(
            title = errorTitle,
            text = errorMessage,
            onDismiss = { showErrorDialog = false }
        )
    }

    ScreenWrapper(onBackClick = onBackClick) {
        Text(
            text = stringResource(id = R.string.login_title),
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        BooktimeTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = stringResource(id = R.string.email_placeholder),
            modifier = Modifier.padding(bottom = 16.dp),
        )

        BooktimeTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = stringResource(id = R.string.password_placeholder),
            modifier = Modifier.padding(bottom = 8.dp),
            visualTransformation = PasswordVisualTransformation(),
        )

        TextButton(
            onClick = onForgotPasswordClick,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = stringResource(id = R.string.forgot_password_link),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        BooktimeButton(
            text = stringResource(id = R.string.login_button),
            isLoading = isLoading,
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = errorEmptyFields
                    showErrorDialog = true
                } else if (!ValidationUtils.isValidEmail(email)) {
                    errorMessage = errorInvalidEmail
                    showErrorDialog = true
                } else {
                    isLoading = true
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                onLoginSuccess()
                            } else {
                                val exception = task.exception
                                errorMessage = when (exception) {
                                    is FirebaseAuthInvalidUserException -> errorUserNotFound
                                    is FirebaseAuthInvalidCredentialsException -> errorWrongPassword
                                    else -> errorLoginGeneric.format(exception?.message)
                                }
                                showErrorDialog = true
                            }
                        }
                }
            }
        )
    }
}
