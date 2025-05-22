package com.panashecare.assistant.viewModel.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun sendPasswordResetEmail() {
        val email = _uiState.value.email.trim()
        if (email.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your email")
            return
        }

        viewModelScope.launch {
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Reset link sent to $email",
                        errorMessage = ""
                    )
                }
                .addOnFailureListener { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = e.localizedMessage ?: "Something went wrong",
                        successMessage = ""
                    )
                }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = "", successMessage = "")
    }
}

data class ForgotPasswordUiState(
    val email: String = "",
    val errorMessage: String = "",
    val successMessage: String = ""
)
