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
import com.panashecare.assistant.model.repository.ShiftResult
import com.panashecare.assistant.model.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ShiftsOverviewViewModel(
    private val shiftRepository: ShiftRepository,
    private val userRepository: UserRepository,
    private val userId: String
): ViewModel() {

    var state by mutableStateOf(ShiftsOverviewState())
    private var pastShiftListState = MutableStateFlow<ShiftResult>(ShiftResult.Loading)
    private var futureShiftListState = MutableStateFlow<ShiftResult>(ShiftResult.Loading)
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    init{
        loadUser(userId)
        loadShiftsOnUserUpdate()
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

    private fun loadShiftsOnUserUpdate() {
        viewModelScope.launch {
            user.collectLatest { currentUser ->
                if (currentUser != null) {
                    loadAllPastShifts(currentUser)
                    loadAllFutureShifts(currentUser)
                } else {
                    // Optionally reset shift states if user becomes null
                    pastShiftListState.value = ShiftResult.Loading
                    pastShiftListState.value = ShiftResult.Loading
                    state = state.copy(shiftsList = emptyList())
                }
            }
        }
    }

    private fun loadAllPastShifts(user: User){
        viewModelScope.launch {
            shiftRepository.getLatestPastShift(loadFullList = true, user = user).collect { shifts ->
                pastShiftListState.value = shifts
                if (shifts is ShiftResult.Success) {
                    state = state.copy(shiftsList = shifts.shiftList)
                }
            }
        }
    }

    private fun loadAllFutureShifts(user: User){
        viewModelScope.launch {
            shiftRepository.getLatestFutureShift(loadFullList = true, user = user).collect { shifts ->
                futureShiftListState.value = shifts
                if (shifts is ShiftResult.Success) {
                    state = state.copy(shiftsList = shifts.shiftList)
                }
            }
        }
    }

    fun onSelectedShiftFocus(shift: Shift){
        state = state.copy(selectedShift = shift)
    }

    fun updateShiftStatus(){
        viewModelScope.launch {
            shiftRepository.updateShiftStatus(state.selectedShift?.id!!){ success ->
                 Log.d("ShiftsOverviewViewModel", "Shift status updated successfully: $success")
            }
        }
    }

    fun onUpcomingShiftsChange(newValue: Boolean){
        state = state.copy(upcomingShifts = newValue)

        if (state.upcomingShifts){
            loadAllFutureShifts(user.value!!)
        }

        if (!state.upcomingShifts){
            loadAllPastShifts(user.value!!)
        }

    }


}

class ShiftsOverviewViewModelFactory(private val shiftRepository: ShiftRepository, private val userRepository: UserRepository ,private val userId: String): ViewModelProvider.Factory{

    // checking that the viewmodel can be created
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ShiftsOverviewViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ShiftsOverviewViewModel(shiftRepository, userRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}

data class ShiftsOverviewState(
    val shiftsList: List<Shift> = emptyList(),
    val selectedShift: Shift? = null,
    val upcomingShifts: Boolean = true,
    val user: User? = null
)