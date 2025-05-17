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
import java.time.LocalTime
import java.time.format.DateTimeFormatter


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

    fun validateFields(): Boolean {
        val errors = mutableMapOf<String, String>()

        // Morning
        val morningTime = screenState.value.morning.time
        if (morningTime.isNullOrEmpty() || !validateTime(morningTime)) {
            errors["morningTime"] = "Please enter a valid morning time (HH:mm)"
        } else if (!isTimeInRange(morningTime, 6, 11)) {
            errors["morningTime"] = "Morning time must be between 06:00 and 11:59"
        }

        if (screenState.value.morning.prescriptionDetails.any { it.selectedMedication.name == "Select" }) {
            errors["morningMedication"] = "Please select a medication for the morning"
        }
        if (screenState.value.morning.prescriptionDetails.any { it.dosage == 0 }) {
            errors["morningDosage"] = "Please enter a dosage for the morning medication(s)"
        }
        if (screenState.value.morning.prescriptionDetails.any { it.selectedUnits.isEmpty() }) {
            errors["morningUnits"] = "Please select units for the morning medication(s)"
        }

        // Afternoon
        val afternoonTime = screenState.value.afternoon.time
        if (afternoonTime.isNullOrEmpty() || !validateTime(afternoonTime)) {
            errors["afternoonTime"] = "Please enter a valid afternoon time (HH:mm)"
        } else if (!isTimeInRange(afternoonTime, 12, 16)) {
            errors["afternoonTime"] = "Afternoon time must be between 12:00 and 16:59"
        }

        if (screenState.value.afternoon.prescriptionDetails.any { it.selectedMedication.name == "Select" }) {
            errors["afternoonMedication"] = "Please select a medication for the afternoon"
        }
        if (screenState.value.afternoon.prescriptionDetails.any { it.dosage == 0 }) {
            errors["afternoonDosage"] = "Please enter a dosage for the afternoon medication(s)"
        }
        if (screenState.value.afternoon.prescriptionDetails.any { it.selectedUnits.isEmpty() }) {
            errors["afternoonUnits"] = "Please select units for the afternoon medication(s)"
        }

        // Evening
        val eveningTime = screenState.value.evening.time
        if (eveningTime.isNullOrEmpty() || !validateTime(eveningTime)) {
            errors["eveningTime"] = "Please enter a valid evening time (HH:mm)"
        } else if (!isTimeInRange(eveningTime, 17, 21)) {
            errors["eveningTime"] = "Evening time must be between 17:00 and 21:59"
        }

        if (screenState.value.evening.prescriptionDetails.any { it.selectedMedication.name == "Select" }) {
            errors["eveningMedication"] = "Please select a medication for the evening"
        }
        if (screenState.value.evening.prescriptionDetails.any { it.dosage == 0 }) {
            errors["eveningDosage"] = "Please enter a dosage for the evening medication(s)"
        }
        if (screenState.value.evening.prescriptionDetails.any { it.selectedUnits.isEmpty() }) {
            errors["eveningUnits"] = "Please select units for the evening medication(s)"
        }

        _screenState.update { it.copy(errors = errors) }
        return errors.isEmpty()
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
        validateFields()
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
                currentState
            }
        }

        validateFields()
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
        validateFields()
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
        validateFields()
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
        validateFields()
    }

    private fun isTimeInRange(timeString: String, startHour: Int, endHour: Int): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val time = LocalTime.parse(timeString, formatter)
            val start = LocalTime.of(startHour, 0)
            val end = LocalTime.of(endHour, 59)
            time in start..end
        } catch (e: Exception) {
            false
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
    val evening: PrescriptionTimeState = PrescriptionTimeState(),
    val errors: Map<String, String> = emptyMap(),
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