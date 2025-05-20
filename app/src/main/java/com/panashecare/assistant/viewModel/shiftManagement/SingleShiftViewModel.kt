package com.panashecare.assistant.viewModel.shiftManagement

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.panashecare.assistant.model.objects.Shift
import com.panashecare.assistant.model.objects.User
import com.panashecare.assistant.model.repository.ShiftRepository
import com.panashecare.assistant.model.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SingleShiftViewModel(
    private val shiftRepository: ShiftRepository,
    private val userRepository: UserRepository,
    private val userId: String
) : ViewModel() {

    var state by  mutableStateOf(SingleShiftState())
        private set
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    init {
        loadUser(userId)
    }

    private fun loadUser(userId: String) {
        viewModelScope.launch {
            userRepository.getUserById(userId) { user ->
                if (user != null) {
                    _user.value = user
                    state = state.copy(user = user)
                }

            }
        }
    }

    fun getShiftById(shiftId: String) {
        shiftRepository.getShiftById(shiftId) { shift ->
            if (shift != null) {
                loadShiftDetails(shift)
            }
        }
    }

    private fun loadShiftDetails(shift: Shift){
        state = state.copy(startDate = shift.shiftDate!!)
        state = state.copy(startTime = shift.shiftTime!!)
        state = state.copy(endDate = shift.shiftEndDate!!)
        state = state.copy(endTime = shift.shiftEndTime!!)
        state = state.copy(profileImageRef = shift.healthAideName?.profileImageRef)

        state = state.copy(healthAideName = shift.healthAideName?.getFullName()!!)

        if (!shift.adminName?.patientFirstName.isNullOrEmpty() && !shift.adminName?.patientLastName.isNullOrEmpty()) {
            state = state.copy(patientName = "${shift.adminName?.patientFirstName} ${shift.adminName?.patientLastName}")
        } else {
            state = state.copy(patientName = "Your Patient")
        }
    }
}

class SingleShiftViewModelFactory(private val shiftRepository: ShiftRepository, private val userRepository: UserRepository,private val userId: String) :  ViewModelProvider.Factory {

    // checking that the viewmodel can be created
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SingleShiftViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SingleShiftViewModel(shiftRepository, userRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}

data class SingleShiftState(
    val startDate: String = "",
    val startTime: String = "",
    val endDate: String = "",
    val endTime: String = "",
    val healthAideName: String = "",
    val patientName: String = "",
    val shiftCountdown: String = "",
    val profileImageRef: Int? = null,
    val user: User? = null
)