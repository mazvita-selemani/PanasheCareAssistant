package com.panashecare.assistant.viewModel.vitals

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.panashecare.assistant.model.objects.Vitals
import com.panashecare.assistant.model.repository.VitalsRepository
import kotlinx.coroutines.launch

class LogVitalsViewModel(
    private val vitalsRepository: VitalsRepository
): ViewModel() {

    var state by mutableStateOf(LogVitalsState())

    fun updateHeartRate(newValue: String){
        state = state.copy(heartRateRecord = newValue)
    }

    fun updateOxygenSaturationRecord(newValue: String){
        state = state.copy(oxygenSaturationRecord = newValue)
    }

    fun updateBloodPressureRecord(newValue: String){
        state = state.copy(bloodPressureRecord = newValue)
    }

    fun submitLog(vitals: Vitals) = viewModelScope.launch{
        vitalsRepository.submitVitalsLog(vitals = vitals){ success ->
            if (success) {
                Log.d("Firebase", "Shift created!")

            } else {
                Log.e("Firebase", "Shift creation failed.")
            }
        }
    }

}

class LogVitalsViewModelFactory(private val vitalsRepository: VitalsRepository): ViewModelProvider.Factory{

    // checking that the viewmodel can be created
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LogVitalsViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return LogVitalsViewModel(vitalsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}

data class LogVitalsState(
    val heartRateRecord: String? = null,
    val oxygenSaturationRecord: String? =null,
    val bloodPressureRecord: String? = null
)