package com.panashecare.assistant.viewModel.authentication

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.panashecare.assistant.model.objects.User
import com.panashecare.assistant.model.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    var state by mutableStateOf(RegisterUiState())

    fun onAdminCheckedChange(newValue: Boolean) {
        state = state.copy(isAdmin = newValue)
    }

    fun onFirstNameChange(newValue: String) {
        state = state.copy(firstName = newValue)
    }

    fun onLastNameChange(newValue: String) {
        state = state.copy(lastName = newValue)
    }

    fun onEmailChange(newValue: String) {
        state = state.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        state = state.copy(password = newValue)
    }

    fun onConfirmPasswordChange(newValue: String) {
        state = state.copy(confirmPassword = newValue)
    }

    fun onPatientFirstNameChange(newValue: String) {
        state = state.copy(patientFirstName = newValue)
    }

    fun onPatientLastNameChange(newValue: String) {
        state = state.copy(patientLastName = newValue)
    }

    fun onPhoneNumberChange(newValue: String) {
        state = state.copy(phoneNumber = newValue)
    }

    fun registerNewUser(user: User) {

        // if information is not valid user will not be registered
        if (!validateFields()) return

        // if the user is not admin any information entered in patient section of form will be erased
        if (!state.isAdmin) {
            state = state.copy(patientFirstName = "", patientLastName = "")
        }

        viewModelScope.launch {
            userRepository.saveUser(user) { success ->
                if (success) {
                    Log.d("Firebase", "User saved!")
                } else {
                    Log.e("Firebase", "User save failed.")
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        val errors = mutableMapOf<String, String>()

        if (state.firstName.isBlank() || !state.firstName[0].isUpperCase()) {
            errors["firstName"] = "First name must start with a capital letter"
        }
        if (state.lastName.isBlank() || !state.lastName[0].isUpperCase()) {
            errors["lastName"] = "Last name must start with a capital letter"
        }

        if (!state.phoneNumber.startsWith("+44") || state.phoneNumber.length != 13 || !state.phoneNumber
            .drop(1).all { it.isDigit() }
        ) {
            errors["phoneNumber"] =
                "Phone number must start with +44 and contain 10 digits after it"
        }

        val passwordRegex = Regex("^(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-={}:\";'<>?,./]).{8,}$")
        if (!passwordRegex.matches(state.password)) {
            errors["password"] =
                "Password must be 8+ characters with a number and a special character"
        }

        if (state.password != state.confirmPassword) {
            errors["confirmPassword"] = "Passwords do not match"
        }

        if (state.isAdmin) {
            if (state.patientFirstName.isBlank()) {
                errors["patientFirstName"] = "Required for Admin"
            }
            if (!state.patientFirstName[0].isUpperCase()) {
                errors["patientFirstName"] = "First name must start with a capital letter"
            }
            if (state.patientLastName.isBlank()) {
                errors["patientLastName"] = "Required for Admin"
            }
            if (!state.patientLastName[0].isUpperCase()) {
                errors["patientLastName"] = "Last name must start with a capital letter"
            }
        }

        state = state.copy(errors = errors)
        return errors.isEmpty()
    }


}

class RegisterViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {

    // checking that the viewmodel can be created
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}


data class RegisterUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val patientFirstName: String = "",
    val patientLastName: String = "",
    val phoneNumber: String = "",
    val isAdmin: Boolean = false,
    val password: String = "",
    val confirmPassword: String = "",
    val errors: Map<String, String> = emptyMap()
)
