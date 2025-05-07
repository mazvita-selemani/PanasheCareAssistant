package com.panashecare.assistant.viewModel.authentication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.panashecare.assistant.model.objects.User
import com.panashecare.assistant.model.repository.UserRepository

class LoginViewModel(private val repository: UserRepository): ViewModel() {

    var state by mutableStateOf(RegisterUiState())

    fun onEmailChange(newValue: String){
        state = state.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String){
        state = state.copy(password = newValue)
    }

    fun getUserAfterLogin(email: String, onUserFound: (User?) -> Unit) {
        repository.getUserByEmail(email, onUserFound)
    }
}

class LoginViewModelFactory(private val repository: UserRepository): ViewModelProvider.Factory{

    // checking that the viewmodel can be created
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LoginViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}

data class LoginUiState (
    val email : String = "",
    val password: String ="",
)