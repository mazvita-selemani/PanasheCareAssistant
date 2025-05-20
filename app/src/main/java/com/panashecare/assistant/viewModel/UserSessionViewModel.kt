package com.panashecare.assistant.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class UserSessionViewModel : ViewModel() {
    private val _userId = mutableStateOf<String?>(null)
    val userId: String? get() = _userId.value

    fun setUserId(id: String) {
        _userId.value = id
    }
}

