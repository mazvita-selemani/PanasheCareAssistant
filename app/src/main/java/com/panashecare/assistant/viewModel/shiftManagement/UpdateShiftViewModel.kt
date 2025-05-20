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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
class UpdateShiftViewModel(
    private val userRepository: UserRepository,
    private val shiftRepository: ShiftRepository
): ViewModel() {

    var state by mutableStateOf(UpdateShiftState())
        private set


    lateinit var initialState: UpdateShiftState

    init {
        getCarers()
    }

    private fun getCarers() = viewModelScope.launch {
        userRepository.getCarers().collect { carers ->
            state = state.copy(carers = carers)
        }

    }

    fun getShiftById(shiftId: String) {
        shiftRepository.getShiftById(shiftId) { shift ->
            if (shift != null) {
                loadShiftDetails(shift)
                state = state.copy(originalShift = shift)
            }
        }
    }

    private fun loadShiftDetails(shift: Shift){
        state = state.copy(startDate = convertStringToLong(shift.shiftDate!!))
        state = state.copy(startTime = convertStringToTimePickerState(shift.shiftTime!!))
        state = state.copy(endDate = convertStringToLong(shift.shiftEndDate!!))
        state = state.copy(endTime = convertStringToTimePickerState(shift.shiftEndTime!!))
        state = state.copy(healthAideName = shift.healthAideName?.getFullName()!!)
        state = state.copy(profileImageRef = shift.healthAideName.profileImageRef)
        state = state.copy(phoneNumber = shift.healthAideName.phoneNumber!!)

        initialState = state

    }

    private fun convertStringToTimePickerState(timeString: String): TimePickerState {
        val parts = timeString.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        return TimePickerState(hour, minute, is24Hour = true)
    }

    private fun convertStringToLong(dateString: String): Long {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

        val localDate = LocalDate.parse(dateString, formatter)
        val zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return zonedDateTime
    }

    fun updateShift(shiftId: String, updatedFields: Map<String, Any?>) {

        if (!validateFields()) return

        viewModelScope.launch {
            shiftRepository.updateShiftFields(
                shiftId = shiftId, updatedFields = updatedFields
            ) { success ->
                if (success) {
                    Log.d("Firebase", "Shift updated successfully!")

                } else {
                    Log.e("Firebase", "Shift update failed.")
                }
            }
        }
    }

    fun validateFields(): Boolean {
        val errors = mutableMapOf<String, String>()

        // Time pickers (null or unselected times)
        if (state.startTime == null) {
            errors["startTime"] = "Please select start time"
        }

        if (state.endTime == null) {
            errors["endTime"] = "Please select end time"
        }

        // Date pickers (null means not selected)
        if (state.startDate == null) {
            errors["startDate"] = "Please select start date"
        }

        if (state.endDate == null) {
            errors["endDate"] = "Please select end date"
        }

        if (state.showDropDownMenu) {
            if(state.selectedCarer == null) {
                errors["selectedCarer"] = "Please select a carer"
            }
        }


        // Check if end date is before start date
        if (state.startDate != null && state.endDate != null) {
            if (state.endDate!! < state.startDate!!) {
                errors["endDate"] = "End date not valid"
            }
        }

        state = state.copy(errors = errors)
        return errors.isEmpty()
    }

    private fun onStateChange() {
        if(state == initialState){
            state = state.copy(haveDetailsChanged = false)
            return
        }

        state = state.copy(haveDetailsChanged = true)
    }

    fun updateChecked(newValue: Boolean) {
        state = state.copy(isChecked = newValue)

        if (state.isChecked) {
            state = state.copy(endDate = state.startDate)
        }

        onStateChange()
    }

    fun updateIsExpanded(newValue: Boolean) {
        state = state.copy(isDropDownMenuExpanded = !newValue)
        onStateChange()
    }

    fun updateShowDropDownMenu(newValue: Boolean) {
        state = state.copy(showDropDownMenu = !newValue)
    }


    fun updateSelectedCarer(user: User) {
        state = state.copy(selectedCarer = user)
        onStateChange()
    }

    fun confirmSelectedCarer() {
        state = state.copy(healthAideName = state.selectedCarer?.getFullName()!!)
        state = state.copy(profileImageRef = state.selectedCarer?.profileImageRef)
        state = state.copy(phoneNumber = state.selectedCarer?.phoneNumber!!)
        onStateChange()
    }

    fun cancelSelectedCarer(shift: Shift) {
        state = state.copy(healthAideName = shift.healthAideName?.getFullName()!!)
        onStateChange()
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
        onStateChange()
    }


    fun updateStartTime(timePickerState: TimePickerState) {
        state = state.copy(startTime = timePickerState)
        onStateChange()
    }


    fun updateEndDate(dateInMillis: Long) {
        state = state.copy(endDate = dateInMillis)
        onStateChange()
    }


    fun updateEndTime(timePickerState: TimePickerState) {
        state = state.copy(endTime = timePickerState)
        onStateChange()
    }

}

class UpdateShiftViewModelFactory(
    private val repository: UserRepository,
    private val shiftRepository: ShiftRepository
) : ViewModelProvider.Factory {

    // checking that the viewmodel can be created
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpdateShiftViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UpdateShiftViewModel(repository, shiftRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}


data class UpdateShiftState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val carers: List<User>? = null,
    val selectedCarer: User? = null,
    val healthAideName: String = "", // current assigned carer
    val phoneNumber: String = "",
    val profileImageRef: Int? = null,
    val isDropDownMenuExpanded: Boolean = false,
    val showDropDownMenu: Boolean = false,
    val originalShift: Shift? = null,
    override val showStartDatePicker: Boolean = false,
    override val showStartTimePicker: Boolean = false,
    override val showEndDatePicker: Boolean = false,
    override val showEndTimePicker: Boolean = false,
    override val startDate: Long? = null,
    override val startTime: TimePickerState? = null,
    override val endDate: Long? = null,
    override val endTime: TimePickerState? = null,
    override val errors: Map<String, String> = emptyMap(),
    override val isChecked: Boolean = false,
    val haveDetailsChanged: Boolean = false
) : ShiftScheduleState