package com.panashecare.assistant.viewModel.shiftManagement

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class CreateShiftViewModel(
    private val userRepository: UserRepository,
    private val shiftRepository: ShiftRepository
) : ViewModel() {

    var state by mutableStateOf(CreateShiftState())

    init {
        getCarers()
    }

    private fun getCarers() = viewModelScope.launch {
        userRepository.getCarers().collect { carers ->
            state = state.copy(carers = carers)
            Log.d("Carers Loading", "You have a list of ${state.carers?.size} carers")
        }

    }

    fun createShift(shift: Shift) = viewModelScope.launch {
        shiftRepository.createShift(
            shift = shift,
        ) { success ->
            if (success) {
                Log.d("Firebase", "Shift created!")

            } else {
                Log.e("Firebase", "Shift creation failed.")
            }
        }
    }

    fun updateIsExpanded(newValue: Boolean) {
        state = state.copy(isExpanded = !newValue)

        Log.d("It works value changed", "${state.isExpanded}")
    }


    fun updateSelectedCarer(user: User) {
        state = state.copy(selectedCarer = user)
    }


    fun updateNotes(newNotes: String) {
        state = state.copy(notes = newNotes)
    }


    fun showStartDatePicker(show: Boolean) {
        state = state.copy(showStartDatePicker = show)
    }


    fun showStartTimePicker(show: Boolean) {
        state = state.copy(showStartTimePicker = show)
    }


    fun showEndDatePicker(show: Boolean) {
        state = state.copy(showEndDatePicker = show)
    }


    fun showEndTimePicker(show: Boolean) {
        state = state.copy(showEndTimePicker = show)
    }


    fun updateStartDate(dateInMillis: Long) {
        state = state.copy(startDate = dateInMillis)
    }


    fun updateStartTime(timePickerState: TimePickerState) {
        state = state.copy(startTime = timePickerState)
    }


    fun updateEndDate(dateInMillis: Long) {
        state = state.copy(endDate = dateInMillis)
    }


    fun updateEndTime(timePickerState: TimePickerState) {
        state = state.copy(endTime = timePickerState)
    }

}

class CreateShiftViewModelFactory(
    private val repository: UserRepository,
    private val shiftRepository: ShiftRepository
) : ViewModelProvider.Factory {

    // checking that the viewmodel can be created
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateShiftViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateShiftViewModel(repository, shiftRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}


data class CreateShiftState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val carers: List<User>? = null,
    val selectedCarer: User? = null,
    val notes: String = "",
    val isExpanded: Boolean = false,
    override val showStartDatePicker: Boolean = false,
    override val showStartTimePicker: Boolean = false,
    override val showEndDatePicker: Boolean = false,
    override val showEndTimePicker: Boolean = false,
    override val startDate: Long? = null,
    override val startTime: TimePickerState? = null,
    override val endDate: Long? = null,
    override val endTime: TimePickerState? = null
) : ShiftScheduleState