package com.panashecare.assistant.viewModel.shiftManagement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.panashecare.assistant.model.objects.Shift
import com.panashecare.assistant.model.repository.ShiftRepository
import com.panashecare.assistant.model.repository.ShiftResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ShiftsOverviewViewModel(
    private val shiftRepository: ShiftRepository
): ViewModel() {

    var state by mutableStateOf(ShiftsOverviewState())
    private var pastShiftListState = MutableStateFlow<ShiftResult>(ShiftResult.Loading)
    private var futureShiftListState = MutableStateFlow<ShiftResult>(ShiftResult.Loading)

    init{
        loadAllFutureShifts()
    }

    private fun loadAllPastShifts(){
        viewModelScope.launch {
            shiftRepository.getLatestPastShift(loadFullList = true).collect { shifts ->
                pastShiftListState.value = shifts
                if (shifts is ShiftResult.Success) {
                    state = state.copy(shiftsList = shifts.shiftList)
                }
            }
        }
    }

    private fun loadAllFutureShifts(){
        viewModelScope.launch {
            shiftRepository.getLatestFutureShift(loadFullList = true).collect { shifts ->
                futureShiftListState.value = shifts
                if (shifts is ShiftResult.Success) {
                    state = state.copy(shiftsList = shifts.shiftList)
                }
            }
        }
    }

    fun onUpcomingShiftsChange(newValue: Boolean){
        state = state.copy(upcomingShifts = newValue)

        if (state.upcomingShifts){
            loadAllFutureShifts()
        }

        if (!state.upcomingShifts){
            loadAllPastShifts()
        }

    }


}

class ShiftsOverviewViewModelFactory(private val shiftRepository: ShiftRepository): ViewModelProvider.Factory{

    // checking that the viewmodel can be created
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ShiftsOverviewViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ShiftsOverviewViewModel(shiftRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}

data class ShiftsOverviewState(
    val shiftsList: List<Shift> = emptyList(),
    val upcomingShifts: Boolean = true
)