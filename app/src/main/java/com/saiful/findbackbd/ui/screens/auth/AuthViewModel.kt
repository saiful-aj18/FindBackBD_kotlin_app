package com.saiful.findbackbd.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saiful.findbackbd.data.model.Role
import com.saiful.findbackbd.data.model.User
import com.saiful.findbackbd.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val role: Role = Role.USER,
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui.asStateFlow()

    val currentUser: StateFlow<User?> = authRepository.currentUser

    fun setRole(r: Role) {
        _ui.value = _ui.value.copy(role = r, error = null)
    }

    fun onEmail(v: String) {
        _ui.value = _ui.value.copy(email = v, error = null)
    }

    fun onPassword(v: String) {
        _ui.value = _ui.value.copy(password = v, error = null)
    }

    fun reset() {
        authRepository.logout()
        _ui.value = AuthUiState()
    }

    fun login(onSuccess: (Role) -> Unit) {
        val s = _ui.value
        if (s.email.isBlank() || s.password.length < 6) {
            _ui.value = s.copy(error = "Enter a valid email/phone and a password of at least 6 characters.")
            return
        }
        _ui.value = s.copy(isLoading = true, error = null)
        viewModelScope.launch {
            authRepository.login(
                emailOrPhone = s.email,
                password = s.password,
                requestedRole = s.role
            ).onSuccess { user ->
                _ui.value = _ui.value.copy(isLoading = false, error = null, password = "")
                onSuccess(user.role)
            }.onFailure { err ->
                _ui.value = _ui.value.copy(
                    isLoading = false,
                    error = err.message ?: "Login failed. Please check your credentials."
                )
            }
        }
    }

    fun loginWithGoogle(onSuccess: (Role) -> Unit) {
        val emailHint = _ui.value.email
        _ui.value = _ui.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            authRepository.loginWithGoogle(emailHint)
                .onSuccess { user ->
                    _ui.value = _ui.value.copy(isLoading = false, error = null)
                    onSuccess(user.role)
                }
                .onFailure { err ->
                    _ui.value = _ui.value.copy(isLoading = false, error = err.message)
                }
        }
    }

    fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        onSuccess: (Role) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            authRepository.register(
                name = name,
                email = email,
                phone = phone,
                password = password
            ).onSuccess { user ->
                _ui.value = AuthUiState(role = user.role, email = user.email)
                onSuccess(user.role)
            }.onFailure { err ->
                onError(err.message ?: "Registration failed.")
            }
        }
    }

    fun sendPasswordReset(email: String, onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.reset(email)
            onDone()
        }
    }

    fun updateProfile(
        name: String,
        email: String,
        phone: String,
        onResult: (Result<User>) -> Unit = {}
    ) {
        viewModelScope.launch {
            val res = authRepository.updateProfile(name, email, phone)
            onResult(res)
        }
    }

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            val res = authRepository.changePassword(currentPassword, newPassword)
            onResult(res)
        }
    }
}
