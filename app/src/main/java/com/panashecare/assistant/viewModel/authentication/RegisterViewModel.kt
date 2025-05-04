package com.panashecare.assistant.viewModel.authentication

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.panashecare.assistant.model.repository.UserRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.panashecare.assistant.model.objects.User
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val userRepository: UserRepository
): ViewModel(){

    var state by mutableStateOf(RegisterUiState())

    fun onAdminCheckedChange(newValue: Boolean){
       state = state.copy(isAdmin = newValue)
    }

    fun onFirstNameChange(newValue: String){
        state = state.copy(firstName = newValue)
    }

    fun onLastNameChange(newValue: String){
        state = state.copy(lastName = newValue)
    }

    fun onEmailChange(newValue: String){
        state = state.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String){
        state = state.copy(password = newValue)
    }

    fun onConfirmPasswordChange(newValue: String){
        state = state.copy(confirmPassword = newValue)
    }

    fun onPatientFirstNameChange(newValue: String){
        state = state.copy(patientFirstName = newValue)
    }

    fun onPatientLastNameChange(newValue: String){
        state = state.copy(patientLastName = newValue)
    }

    fun onPhoneNumberChange(newValue: String){
        state = state.copy(phoneNumber = newValue)
    }

    fun registerNewUser(user: User) {

        // if the user is not admin any information entered in patient section of form will be erased
        if (!state.isAdmin){
            state = state.copy(patientFirstName = "", patientLastName = "")
        }

        viewModelScope.launch {
            userRepository.saveUser(user) { success ->
                if (success) {
                    Log.d("Firebase", "User saved!")
                    // Do next action here
                } else {
                    Log.e("Firebase", "User save failed.")
                }
            }
        }
    }


}

class RegisterViewModelFactory(private val repository: UserRepository): ViewModelProvider.Factory{

    // checking that the viewmodel can be created
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}


data class RegisterUiState (
    val firstName: String = "",
    val lastName: String = "",
    val email : String = "",
    val patientFirstName : String = "",
    val patientLastName : String = "",
    val phoneNumber : String = "",
    val isAdmin : Boolean = false,
    val password: String ="",
    val confirmPassword: String ="",
)