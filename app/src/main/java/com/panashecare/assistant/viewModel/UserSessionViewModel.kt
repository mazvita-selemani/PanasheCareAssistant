package com.panashecare.assistant.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.panashecare.assistant.model.repository.UserRepository

class UserSessionViewModel : ViewModel() {
    private val _userId = mutableStateOf<String?>(null)
    val userId: String? get() = _userId.value

    init {
        Log.d("UserSessionViewModel", "User ID: ${_userId.value}")
    }

    fun setUserId(id: String) {
        _userId.value = id
        Log.d("UserSessionViewModel", "User ID: ${_userId.value}")

    }

    fun clearUserId() {
        _userId.value = null
    }

    fun fetchAndSetCustomUserId(email: String, userRepository: UserRepository, onResult: (Boolean) -> Unit) {
        userRepository.getCustomUserIdByEmail(email) { customId ->
            if (customId != null) {
                _userId.value = customId
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

}

