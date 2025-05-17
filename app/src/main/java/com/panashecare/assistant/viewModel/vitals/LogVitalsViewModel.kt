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

    fun submitLog(vitals: Vitals) {

        if (!validateFields()) return

        viewModelScope.launch{
            vitalsRepository.submitVitalsLog(vitals = vitals){ success ->
                if (success) {
                    Log.d("Firebase", "Shift created!")

                } else {
                    Log.e("Firebase", "Shift creation failed.")
                }
            }
        }
    }

    fun validateFields(): Boolean {
        val errors = mutableMapOf<String, String>()

        // Oxygen Saturation: 0–100 (%)
        val oxygen = state.oxygenSaturationRecord?.toIntOrNull()
        if (oxygen == null || oxygen !in 50..100) {
            errors["oxygenSaturation"] = "Enter a valid oxygen level (50–100%)"
        }

        // Blood Pressure: Expected in format "120/80"
        val bpParts = state.bloodPressureRecord?.split("/")
        val systolic = bpParts?.getOrNull(0)?.toIntOrNull()
        val diastolic = bpParts?.getOrNull(1)?.toIntOrNull()
        if (systolic == null || diastolic == null || systolic !in 90..200 || diastolic !in 60..130) {
            errors["bloodPressure"] = "Enter valid BP in format 120/80"
        }

        // Heart Rate: 40–180 bpm
        val hr = state.heartRateRecord?.toIntOrNull()
        if (hr == null || hr !in 40..180) {
            errors["heartRateRecord"] = "Enter a valid heart rate (40–180 bpm)"
        }

        state = state.copy(errors = errors)
        return errors.isEmpty()
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
    val bloodPressureRecord: String? = null,
    val errors: Map<String, String> = emptyMap()
)