package com.panashecare.assistant.viewModel.medication

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.panashecare.assistant.model.objects.DailyMedicationLog
import com.panashecare.assistant.model.objects.Medication
import com.panashecare.assistant.model.objects.Prescription
import com.panashecare.assistant.model.repository.DailyMedicationLogRepository
import com.panashecare.assistant.model.repository.MedicationRepository
import com.panashecare.assistant.model.repository.MedicationResult
import com.panashecare.assistant.model.repository.PrescriptionRepository
import com.panashecare.assistant.model.repository.PrescriptionResult
import com.panashecare.assistant.model.service.LocalNotificationWorker
import com.panashecare.assistant.utils.TimeSerialisationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class DailyMedicationTrackerViewModel(
    private val prescriptionRepository: PrescriptionRepository,
    private val dailyMedicationLogRepository: DailyMedicationLogRepository,
    private val medicationRepository: MedicationRepository
) : ViewModel() {

    var state by mutableStateOf(DailyMedicationTrackerState())
        private set
    private var prescriptionScheduleState =
        MutableStateFlow<PrescriptionResult>(PrescriptionResult.Loading)
    private val helper = TimeSerialisationHelper()

    private val medication = MutableStateFlow<Medication?>(null)
    private val medicationMap = mutableStateOf<Map<String, Medication>>(emptyMap())
    private var medicationList = MutableStateFlow<MedicationResult>(MedicationResult.Loading)

    init {
        loadPrescriptionSchedule()
        loadAllMedications()
        val currentTimeOfDay = getCurrentTimeOfDay()
        state = state.copy(currentTimeOfDay = currentTimeOfDay)
    }


    private fun loadPrescriptionSchedule() {
        viewModelScope.launch {
            prescriptionRepository.getPrescriptionsRealtime().collect { prescriptions ->
                prescriptionScheduleState.value = prescriptions
                if (prescriptions is PrescriptionResult.Success) {
                    state = state.copy(prescriptions = prescriptions.prescription)
                }
                Log.d("Panashe Meds VM", "DailyMedicationTracker: ${state.prescriptions}")

            }
        }
    }

    private fun loadAllMedications() {
        viewModelScope.launch {
            medicationRepository.getAllMedications().collect { meds ->
                medicationMap.value = meds.associateBy { it.id.toString() }
            }
        }
    }

    fun getMedicationById(medicationId: String): Medication? {
        return medicationMap.value[medicationId]
    }

    fun confirmMedicationIntake(timeOfDay: String) {
        viewModelScope.launch {
            val today = helper.convertDateToString(
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            )

            val (medications, checks) = when (timeOfDay) {
                "morning" -> state.prescriptions?.morningMedication to listOf(
                    state.isMorningFirstChecked,
                    state.isMorningSecondChecked
                )

                "afternoon" -> state.prescriptions?.afternoonMedication to listOf(
                    state.isAfternoonFirstChecked,
                    state.isAfternoonSecondChecked
                )
                "evening" -> state.prescriptions?.eveningMedication to listOf(
                    state.isEveningFirstChecked,
                    state.isEveningSecondChecked
                )
                else -> null to emptyList()
            }

            medications?.forEachIndexed { index, prescriptionItem ->
                val wasTaken = checks.getOrNull(index) ?: false
                val medId = prescriptionItem.medication
                if (medId != null) {
                    submitLogAndUpdateStock(
                        medicationId = medId,
                        wasTaken = wasTaken,
                        timeOfDay = timeOfDay,
                        date = today
                    )
                }
            }
        }
    }

    private fun submitLogAndUpdateStock(
        medicationId: String,
        wasTaken: Boolean,
        timeOfDay: String,
        date: String
    ) {
        val log = DailyMedicationLog(
            date = date,
            medicationId = medicationId,
            wasTaken = wasTaken,
            timeOfDay = timeOfDay.uppercase()
        )

        dailyMedicationLogRepository.submitLog(log) { success ->
            if (success) {
                Log.d("Firebase", "Log submitted successfully.")
            } else {
                Log.e("Firebase", "Log submission failed.")
            }
        }

        if (wasTaken) {
            val medication = medicationMap.value[medicationId]
            val newInventory = (medication?.totalInStock ?: 1) - 1
            medicationRepository.updateMedication(
                medicationId = medicationId,
                newInventoryLevel = newInventory
            ) { success ->
                if (success) {
                    Log.d("Firebase", "Inventory updated successfully.")
                } else {
                    Log.e("Firebase", "Inventory update failed.")
                }
            }
        }
    }

    fun getCurrentTimeOfDay(): String {
        val hour = LocalDateTime.now().hour
        return when (hour) {
            in 5..11 -> "morning"
            in 12..17 -> "afternoon"
            else -> "evening"
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
    val prescriptions: Prescription? = null,
    val currentTimeOfDay: String = "morning"
)

class DailyMedicationTrackerViewModelFactory(
    private val prescriptionRepository: PrescriptionRepository,
    private val dailyMedicationLogRepository: DailyMedicationLogRepository,
    private val medicationRepository: MedicationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DailyMedicationTrackerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DailyMedicationTrackerViewModel(
                prescriptionRepository,
                dailyMedicationLogRepository,
                medicationRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


