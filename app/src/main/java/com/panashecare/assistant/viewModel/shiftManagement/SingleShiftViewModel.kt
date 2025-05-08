package com.panashecare.assistant.viewModel.shiftManagement

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.panashecare.assistant.model.objects.Shift
import com.panashecare.assistant.model.repository.ShiftRepository

class SingleShiftViewModel(private val shiftRepository: ShiftRepository) : ViewModel() {

    var state by  mutableStateOf(SingleShiftState())
        private set

    fun getShiftById(shiftId: String) {
        shiftRepository.getShiftById(shiftId) { shift ->
            Log.d("SingleShiftViewModel", "Shift: $shift")
            Log.d("SingleShiftViewModel", "Shift Id: $shiftId")
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

        state = state.copy(healthAideName = shift.healthAideName?.getFullName()!!)
    }
}

class SingleShiftViewModelFactory(private val shiftRepository: ShiftRepository) :
    ViewModelProvider.Factory {

    // checking that the viewmodel can be created
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SingleShiftViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SingleShiftViewModel(shiftRepository) as T
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
    val shiftCountdown: String = "",
)