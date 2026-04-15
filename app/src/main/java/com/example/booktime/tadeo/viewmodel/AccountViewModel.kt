package com.example.booktime.tadeo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.booktime.tadeo.data.model.User
import com.example.booktime.tadeo.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.net.Uri

sealed class AccountUiState {
    object Idle : AccountUiState()
    object Loading : AccountUiState()
    data class Success(val message: String) : AccountUiState()
    data class Error(val message: String) : AccountUiState()
}

class AccountViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _uiState = MutableStateFlow<AccountUiState>(AccountUiState.Idle)
    val uiState: StateFlow<AccountUiState> = _uiState

    private var originalUser: User? = null

    fun loadUser() {
        _uiState.value = AccountUiState.Loading

        repository.getUser {
            if (it != null) {
                _user.value = it
                originalUser = it
                _uiState.value = AccountUiState.Idle
            } else {
                _uiState.value = AccountUiState.Error("Error al cargar datos")
            }
        }
    }

    fun updateUser(name: String, email: String) {

        if (name.isBlank() || email.isBlank()) {
            _uiState.value = AccountUiState.Error("Campos vacíos")
            return
        }

        _uiState.value = AccountUiState.Loading

        repository.updateUser(name, email) { success ->
            _uiState.value =
                if (success) AccountUiState.Success("Datos actualizados")
                else AccountUiState.Error("Error al actualizar")
        }
    }

    fun changePassword(current: String, new: String) {

        if (current.isBlank() || new.isBlank()) {
            _uiState.value = AccountUiState.Error("Campos vacíos")
            return
        }

        if (new.length < 6) {
            _uiState.value = AccountUiState.Error("Mínimo 6 caracteres")
            return
        }

        _uiState.value = AccountUiState.Loading

        repository.changePassword(current, new) { success, message ->
            _uiState.value =
                if (success) AccountUiState.Success(message)
                else AccountUiState.Error(message)
        }
    }

    fun resetUser() {
        _user.value = originalUser
        _uiState.value = AccountUiState.Idle
    }

    fun logout() {
        repository.logout()
    }


    fun uploadImage(uri: Uri) {
        repository.uploadProfileImage(uri) { url ->
            if (url != null) {
                loadUser()
            }
        }
    }
}