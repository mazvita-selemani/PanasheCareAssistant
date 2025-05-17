package com.panashecare.assistant.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.panashecare.assistant.model.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileDetailsViewModel(private val userRepository: UserRepository, private val userId: String) : ViewModel() {

    init{
        loadUserDetails(userId)
    }

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    private fun loadUserDetails(userId: String) {
        userRepository.getUserById(userId) { user ->
            if (user != null) {
                _state.update { it.copy(fullName = "${user.firstName} ${user.lastName}") }
                _state.update { it.copy(patientName = "${user.patientFirstName} ${user.patientLastName}") }
                _state.update { it.copy(email = user.email ?: "") }
                _state.update { it.copy(phone = user.phoneNumber ?: "") }
                copyToOriginalValues()
            }
        }
    }

    private fun copyToOriginalValues(){
        _state.update { it.copy(originalFullName = it.fullName) }
        _state.update { it.copy(originalPatientName = it.patientName) }
        _state.update { it.copy(originalEmail = it.email) }
        _state.update { it.copy(originalPhone = it.phone) }
        _state.update { it.copy(originalValues = it.currentValues) }
    }

    fun onFullNameChange(newValue: String) {
        _state.update { it.copy(fullName = newValue) }
    }

    fun onPatientNameChange(newValue: String) {
        _state.update { it.copy(patientName = newValue) }
    }

    fun onEmailChange(newValue: String) {
        _state.update { it.copy(email = newValue) }
    }

    fun onPhoneChange(newValue: String) {
        _state.update { it.copy(phone = newValue) }
        Log.d("ProfileDetailsViewModel", "${_state.value.currentValues}")
        Log.d("ProfileDetailsViewModel", "${_state.value.originalValues}")
        Log.d("ProfileDetailsViewModel", "${_state.value.hasChanges}")
    }

    private fun splitName(name: String): Pair<String, String> {
        val parts = name.trim().split(" ")
        val firstName = parts.firstOrNull() ?: ""
        val lastName = parts.drop(1).joinToString(" ")
        return firstName to lastName
    }

    private fun getUpdatedFields(): Map<String, String> {
        val current = _state.value
        val updates = mutableMapOf<String, String>()

        if (current.fullName != current.originalFullName) {
            val (firstName, lastName) = splitName(current.fullName)
            updates["firstName"] = firstName
            updates["lastName"] = lastName
        }

        if (current.patientName != current.originalPatientName) {
            val (patientFirst, patientLast) = splitName(current.patientName)
            updates["patientFirstName"] = patientFirst
            updates["patientLastName"] = patientLast
        }

        if (current.phone != current.originalPhone) {
            updates["phoneNumber"] = current.phone
        }

        if (current.email != current.originalEmail) {
            updates["email"] = current.email
        }

        return updates
    }

    fun validateFields(): Boolean {
        val errors = mutableMapOf<String, String>()

        // Validate full name (e.g., "Glenda Mangoma")
        val fullNameParts = _state.value.fullName.trim().split(" ")
        if (fullNameParts.size != 2 || fullNameParts.any { it.length < 3 || !it[0].isUpperCase() }) {
            errors["fullName"] = "Enter a first and last name with at least 3 characters each, starting with capital letters"
        }

        // Validate patient name (e.g., "Panashe Mangoma")
        val patientNameParts = _state.value.patientName.trim().split(" ")
        if (patientNameParts.size != 2 || patientNameParts.any { it.length < 3 || !it[0].isUpperCase() }) {
            errors["patientName"] = "Enter a patient first and last name with at least 3 characters each, starting with capital letters"
        }

        // Validate phone number
        if (!_state.value.phone.startsWith("+44") || _state.value.phone.length != 13 || _state.value.phone.drop(1).any { !it.isDigit() }) {
            errors["phone"] = "Phone number must start with +44 and contain 10 digits after it"
        }

        // Validate email format (basic)
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_state.value.email).matches()) {
            errors["email"] = "Enter a valid email address"
        }

        _state.update { it.copy(errors = errors) }
        return errors.isEmpty()
    }



    fun saveChanges(userId: String) {

        if (!validateFields()) return

        viewModelScope.launch {
            val fieldsToUpdate = getUpdatedFields()
            if (fieldsToUpdate.isNotEmpty()) {
                userRepository.updateUser(id = userId, fields = fieldsToUpdate)
            }
        }
    }

}

data class ProfileState(
    val fullName: String = "",
    val patientName: String = "",
    val email: String = "",
    val phone: String = "",

    // Original full names and other fields for comparison
    val originalFullName: String = "",
    val originalPatientName: String = "",
    val originalEmail: String = "",
    val originalPhone: String = "",
    val originalValues: List<String> = listOf("", "", "", ""),
    val errors: Map<String, String> = emptyMap()
) {
    val currentValues = listOf(fullName, patientName, email, phone)
    val hasChanges: Boolean get() = originalValues != currentValues
}

class ProfileViewModelFactory(private val userRepository: UserRepository, private val userId: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileDetailsViewModel(userRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
