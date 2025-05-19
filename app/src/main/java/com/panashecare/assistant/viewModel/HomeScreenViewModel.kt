package com.panashecare.assistant.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.panashecare.assistant.model.objects.Shift
import androidx.lifecycle.viewModelScope
import com.panashecare.assistant.model.objects.User
import com.panashecare.assistant.model.repository.ShiftRepository
import com.panashecare.assistant.model.repository.ShiftResult
import com.panashecare.assistant.model.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val shiftRepository: ShiftRepository,
    private val userRepository: UserRepository,
    private val userId: String
): ViewModel(){

    var state by mutableStateOf(HomeScreenState())
    val latestShiftState = MutableStateFlow<ShiftResult>(ShiftResult.Loading)
    val latestFutureState = MutableStateFlow<ShiftResult>(ShiftResult.Loading)

    init {
        viewModelScope.launch{
            loadUser(userId)
            loadPastShift()
            loadFutureShift()
        }
        
    }

    private fun loadUser(userId: String) {
        userRepository.getUserById(userId) { user ->
            if (user != null) {
                state = state.copy(user = user)
            }
        }
    }

    private fun loadPastShift() {
        viewModelScope.launch {
            shiftRepository.getLatestPastShift()
                .collect { result ->
                    latestShiftState.value = result
                    if (result is ShiftResult.Success) {
                        state = state.copy(pastShift = result.shift)
                    }
                }
        }
    }

    private fun loadFutureShift() {
        viewModelScope.launch {
            shiftRepository.getLatestFutureShift()
                .collect { result ->
                    latestFutureState.value = result
                    if (result is ShiftResult.Success) {
                        state = state.copy(futureShift = result.shift)
                    }
                }
        }
    }

}

data class HomeScreenState(
    val pastShift: Shift? = null,
    val futureShift: Shift? = null,
    val user: User? = null
)

class HomeScreenViewModelFactory(private val repository: ShiftRepository, private val userRepository: UserRepository ,private val userId: String): ViewModelProvider.Factory{

    // checking that the viewmodel can be created
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeScreenViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return HomeScreenViewModel(repository, userRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}

