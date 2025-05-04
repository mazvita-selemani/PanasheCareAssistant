package com.panashecare.assistant.viewModel.medication

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.panashecare.assistant.model.objects.MedicationWithDosage
import com.panashecare.assistant.model.objects.Prescription
import com.panashecare.assistant.model.repository.PrescriptionRepository
import com.panashecare.assistant.model.repository.PrescriptionResult
import com.panashecare.assistant.model.repository.ShiftResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DailyMedicationTrackerViewModel(private val prescriptionRepository: PrescriptionRepository): ViewModel()  {

    var state by mutableStateOf(DailyMedicationTrackerState())
    private var morningPrescriptionListState = MutableStateFlow<PrescriptionResult>(PrescriptionResult.Loading)
    private var afternoonPrescriptionListState = MutableStateFlow<PrescriptionResult>(PrescriptionResult.Loading)
    private var eveningPrescriptionListState = MutableStateFlow<PrescriptionResult>(PrescriptionResult.Loading)

    init{
        loadMorningPrescriptions()
        loadAfternoonPrescriptions()
        loadEveningPrescriptions()
    }


    private fun loadMorningPrescriptions(){
        viewModelScope.launch {
            prescriptionRepository.getPrescriptionsRealtime("morning").collect { prescriptions ->
                morningPrescriptionListState.value = prescriptions
                if (prescriptions is PrescriptionResult.Success) {
                    state = state.copy(morningPrescriptions = prescriptions.prescriptionList)
                }
                Log.d("Panashe Meds VM", "DailyMedicationTracker: ${state.morningPrescriptions}")

            }
        }
    }

    private fun loadAfternoonPrescriptions(){
        viewModelScope.launch {
            prescriptionRepository.getPrescriptionsRealtime("afternoon").collect { prescriptions ->
                afternoonPrescriptionListState.value = prescriptions
                if (prescriptions is PrescriptionResult.Success) {
                    state = state.copy(afternoonPrescriptions = prescriptions.prescriptionList)
                }
            }
        }
    }

    private fun loadEveningPrescriptions(){
        viewModelScope.launch {
            prescriptionRepository.getPrescriptionsRealtime("evening").collect { prescriptions ->
                eveningPrescriptionListState.value = prescriptions
                if (prescriptions is PrescriptionResult.Success) {
                    state = state.copy(eveningPrescriptions = prescriptions.prescriptionList)
                }

            }
        }
    }

    fun updateMorningFirstChecked(isChecked: Boolean) {
        state = state.copy(isMorningFirstChecked = isChecked)

    }
    fun updateMorningSecondChecked(isChecked: Boolean) {
        state = state.copy(isMorningSecondChecked = isChecked)
    }

    fun updateAfternoonFirstChecked(isChecked: Boolean) {
        state = state.copy(isAfternoonFirstChecked = isChecked)
    }

    fun updateAfternoonSecondChecked(isChecked: Boolean) {
        state = state.copy(isAfternoonSecondChecked = isChecked)
    }

    fun updateEveningFirstChecked(isChecked: Boolean) {
        state = state.copy(isEveningFirstChecked = isChecked)
    }

    fun updateEveningSecondChecked(isChecked: Boolean) {
        state = state.copy(isEveningSecondChecked = isChecked)
    }

}

data class DailyMedicationTrackerState(
    val isMorningFirstChecked: Boolean = false,
    val isMorningSecondChecked: Boolean = false,
    val isAfternoonFirstChecked: Boolean = false,
    val isAfternoonSecondChecked: Boolean = false,
    val isEveningFirstChecked: Boolean = false,
    val isEveningSecondChecked: Boolean = false,
    val morningPrescriptions: List<MedicationWithDosage> = emptyList(),
    val afternoonPrescriptions: List<MedicationWithDosage> = emptyList(),
    val eveningPrescriptions: List<MedicationWithDosage> = emptyList(),

)

class DailyMedicationTrackerViewModelFactory(private val prescriptionRepository: PrescriptionRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DailyMedicationTrackerViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return DailyMedicationTrackerViewModel(prescriptionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


