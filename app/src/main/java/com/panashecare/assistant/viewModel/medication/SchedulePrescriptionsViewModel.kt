package com.panashecare.assistant.viewModel.medication

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.panashecare.assistant.model.objects.Medication
import com.panashecare.assistant.model.objects.Prescription
import com.panashecare.assistant.model.repository.MedicationRepository
import com.panashecare.assistant.model.repository.MedicationResult
import com.panashecare.assistant.model.repository.PrescriptionRepository
import com.panashecare.assistant.model.repository.ShiftResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class PrescriptionViewModel(private val prescriptionRepository: PrescriptionRepository, private val medicationRepository: MedicationRepository, ) : ViewModel() {

    private val _screenState = MutableStateFlow(PrescriptionScreenState())
    val screenState: StateFlow<PrescriptionScreenState> = _screenState
    private var medicationList = MutableStateFlow<MedicationResult>(MedicationResult.Loading)


    init {
        loadMedications()
    }

    // load medications and update the drop down menu for all three times of the day
    private fun loadMedications() {
        viewModelScope.launch {
            medicationRepository.getMedicationsRealtime(). collect{
                medications ->
                medicationList.value = medications
                if (medications is MedicationResult.Success) {
                    _screenState.update { currentState ->
                        currentState.copy(
                            morning = currentState.morning.copy(
                                prescriptionDetails = mutableStateListOf(PrescriptionDetailState(medicationList = medications.MedicationList))
                            ),
                            afternoon = currentState.afternoon.copy(
                                prescriptionDetails = mutableStateListOf(PrescriptionDetailState(medicationList = medications.MedicationList))
                            ),
                            evening = currentState.evening.copy(
                                prescriptionDetails = mutableStateListOf(PrescriptionDetailState(medicationList = medications.MedicationList))
                            )
                        )
                    }
                }
            }

        }
    }

    fun createPrescriptionSchedule(prescription: Prescription)= viewModelScope.launch {
        prescriptionRepository.createPrescription(
            prescription = prescription,
        ) { success ->
            if (success) {
                Log.d("Firebase", "Prescription schedule created!")

            } else {
                Log.e("Firebase", "Prescription schedule creation failed.")
            }
        }
    }

    fun onTimeChanged(timeOfDay: TimeOfDay, time: String) {
        _screenState.update { currentState ->
            val timeState = when (timeOfDay) {
                TimeOfDay.MORNING -> currentState.morning
                TimeOfDay.AFTERNOON -> currentState.afternoon
                TimeOfDay.EVENING -> currentState.evening
            }

            val isTimeValid = validateTime(time)

            val updatedTimeState = timeState.copy(
                time = time,
                isAddButtonEnabled = isTimeValid && timeState.prescriptionDetails.size < 2
            )

            when (timeOfDay) {
                TimeOfDay.MORNING -> currentState.copy(morning = updatedTimeState)
                TimeOfDay.AFTERNOON -> currentState.copy(afternoon = updatedTimeState)
                TimeOfDay.EVENING -> currentState.copy(evening = updatedTimeState)
            }
        }
    }

    fun onAddPrescriptionRow(timeOfDay: TimeOfDay) {
        _screenState.update { currentState ->
            val timeState = when (timeOfDay) {
                TimeOfDay.MORNING -> currentState.morning
                TimeOfDay.AFTERNOON -> currentState.afternoon
                TimeOfDay.EVENING -> currentState.evening
            }

            if (timeState.isAddButtonEnabled && timeState.prescriptionDetails.size < 2) {
                val updatedDetails = timeState.prescriptionDetails.toMutableList().apply {
                    add(PrescriptionDetailState(isVisible = true, medicationList = timeState.prescriptionDetails.firstOrNull()?.medicationList.orEmpty()))
                }
                val updatedTimeState = timeState.copy(
                    prescriptionDetails = mutableStateListOf(*updatedDetails.toTypedArray()),
                    isAddButtonEnabled = updatedDetails.size < 2 && timeState.time?.isNotEmpty() == true && validateTime(timeState.time)
                )
                when (timeOfDay) {
                    TimeOfDay.MORNING -> currentState.copy(morning = updatedTimeState)
                    TimeOfDay.AFTERNOON -> currentState.copy(afternoon = updatedTimeState)
                    TimeOfDay.EVENING -> currentState.copy(evening = updatedTimeState)
                }
            } else {
                currentState // No change if button is disabled or max rows reached
            }
        }
    }

    fun onRemovePrescriptionRow(timeOfDay: TimeOfDay, index: Int) {
        _screenState.update { currentState ->
            val timeState = when (timeOfDay) {
                TimeOfDay.MORNING -> currentState.morning
                TimeOfDay.AFTERNOON -> currentState.afternoon
                TimeOfDay.EVENING -> currentState.evening
            }
            if (timeState.prescriptionDetails.size > 1 && index >= 0 && index < timeState.prescriptionDetails.size) {
                val updatedDetails = timeState.prescriptionDetails.toMutableList().apply {
                    removeAt(index)
                }
                val updatedTimeState = timeState.copy(
                    prescriptionDetails = mutableStateListOf(*updatedDetails.toTypedArray()),
                    isAddButtonEnabled = updatedDetails.size < 2 && timeState.time?.isNotEmpty() == true && validateTime(timeState.time)
                )
                when (timeOfDay) {
                    TimeOfDay.MORNING -> currentState.copy(morning = updatedTimeState)
                    TimeOfDay.AFTERNOON -> currentState.copy(afternoon = updatedTimeState)
                    TimeOfDay.EVENING -> currentState.copy(evening = updatedTimeState)
                }
            } else {
                currentState
            }
        }
    }

    fun onMedicationExpandedChange(timeOfDay: TimeOfDay, index: Int, isExpanded: Boolean) {
        _screenState.update { currentState ->
            val updatedDetails = getCurrentDetailList(timeOfDay).toMutableList().apply {
                if (index in indices) {
                    this[index] = this[index].copy(isMedicationExpanded = isExpanded)
                }
            }
            updateDetailList(currentState, timeOfDay, updatedDetails)
        }
    }

    fun onUnitsExpandedChange(timeOfDay: TimeOfDay, index: Int, isExpanded: Boolean) {
        _screenState.update { currentState ->
            val updatedDetails = getCurrentDetailList(timeOfDay).toMutableList().apply {
                if (index in indices) {
                    this[index] = this[index].copy(isUnitsExpanded = isExpanded)
                }
            }
            updateDetailList(currentState, timeOfDay, updatedDetails)
        }
    }

    fun onDosageChanged(timeOfDay: TimeOfDay, index: Int, dosage: Int) {
        _screenState.update { currentState ->
            val updatedDetails = getCurrentDetailList(timeOfDay).toMutableList().apply {
                if (index in indices) {
                    this[index] = this[index].copy(dosage = dosage)
                }
            }
            updateDetailList(currentState, timeOfDay, updatedDetails)
        }
    }

    fun onSelectMedication(timeOfDay: TimeOfDay, index: Int, medication: Medication) {
        _screenState.update { currentState ->
            val updatedDetails = getCurrentDetailList(timeOfDay).toMutableList().apply {
                if (index in indices) {
                    this[index] = this[index].copy(selectedMedication = medication, isMedicationExpanded = false)
                }
            }
            updateDetailList(currentState, timeOfDay, updatedDetails)
        }
    }

    fun onSelectUnits(timeOfDay: TimeOfDay, index: Int, units: String) {
        _screenState.update { currentState ->
            val updatedDetails = getCurrentDetailList(timeOfDay).toMutableList().apply {
                if (index in indices) {
                    this[index] = this[index].copy(selectedUnits = units, isUnitsExpanded = false)
                }
            }
            updateDetailList(currentState, timeOfDay, updatedDetails)
        }
    }

    private fun getCurrentDetailList(timeOfDay: TimeOfDay): List<PrescriptionDetailState> {
        return when (timeOfDay) {
            TimeOfDay.MORNING -> _screenState.value.morning.prescriptionDetails
            TimeOfDay.AFTERNOON -> _screenState.value.afternoon.prescriptionDetails
            TimeOfDay.EVENING -> _screenState.value.evening.prescriptionDetails
        }
    }

    private fun updateDetailList(
        currentState: PrescriptionScreenState,
        timeOfDay: TimeOfDay,
        updatedDetails: List<PrescriptionDetailState>
    ): PrescriptionScreenState {
        val updatedTimeState = when (timeOfDay) {
            TimeOfDay.MORNING -> currentState.morning.copy(prescriptionDetails = mutableStateListOf(*updatedDetails.toTypedArray()))
            TimeOfDay.AFTERNOON -> currentState.afternoon.copy(prescriptionDetails = mutableStateListOf(*updatedDetails.toTypedArray()))
            TimeOfDay.EVENING -> currentState.evening.copy(prescriptionDetails = mutableStateListOf(*updatedDetails.toTypedArray()))
        }
        return when (timeOfDay) {
            TimeOfDay.MORNING -> currentState.copy(morning = updatedTimeState)
            TimeOfDay.AFTERNOON -> currentState.copy(afternoon = updatedTimeState)
            TimeOfDay.EVENING -> currentState.copy(evening = updatedTimeState)
        }
    }

    private fun validateTime(time: String): Boolean {
        return time.matches(Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]$"))
    }
}

enum class TimeOfDay {
    MORNING,
    AFTERNOON,
    EVENING
}

data class PrescriptionDetailState(
    val isVisible: Boolean = false,
    val medicationList: List<Medication> = emptyList(),
    val dosage: Int = 0,
    val isMedicationExpanded: Boolean = false,
    val isUnitsExpanded: Boolean = false,
    val selectedMedication: Medication = Medication(name = "Select"),
    val selectedUnits: String = ""
)

data class PrescriptionTimeState(
    val time: String? = "",
    val isAddButtonEnabled: Boolean = false,
    val prescriptionDetails: MutableList<PrescriptionDetailState> = mutableStateListOf(PrescriptionDetailState()) // a list of 2 maximum, for the time of day rows
)

data class PrescriptionScreenState(
    val morning: PrescriptionTimeState = PrescriptionTimeState(),
    val afternoon: PrescriptionTimeState = PrescriptionTimeState(),
    val evening: PrescriptionTimeState = PrescriptionTimeState()
)

class PrescriptionViewModelFactory(
    private val prescriptionRepository: PrescriptionRepository,
    private val medicationRepository: MedicationRepository,
): ViewModelProvider.Factory{

    // checking that the viewmodel can be created
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PrescriptionViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return PrescriptionViewModel(prescriptionRepository, medicationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}