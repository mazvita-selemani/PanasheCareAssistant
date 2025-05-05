package com.panashecare.assistant.viewModel.medication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.panashecare.assistant.model.objects.Medication
import com.panashecare.assistant.model.repository.MedicationRepository
import com.panashecare.assistant.model.repository.MedicationResult
import com.panashecare.assistant.model.repository.PrescriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ManageMedicationInventoryViewModel(private val medicationRepository: MedicationRepository) :
    ViewModel() {

    var state by mutableStateOf(ManageInventoryState())
    val medicationList = MutableStateFlow<MedicationResult>(MedicationResult.Loading)


    init {
        loadMedications()
    }

    // load medications and update the drop down menu for all three times of the day
    private fun loadMedications() {
        viewModelScope.launch {
            medicationRepository.getMedicationsRealtime().collect { medications ->
                medicationList.value = medications
                if (medications is MedicationResult.Success) {
                    state = state.copy(medicationList = medications.MedicationList)
                }
            }
        }

    }
}


data class ManageInventoryState(
    val medicationList: List<Medication> = emptyList(),
    val isSaveDisabled: Boolean = false,
)

class ManageMedicationInventoryViewModelFactory(
    private val medicationRepository: MedicationRepository,
): ViewModelProvider.Factory{

    // checking that the viewmodel can be created
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ManageMedicationInventoryViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ManageMedicationInventoryViewModel(medicationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}