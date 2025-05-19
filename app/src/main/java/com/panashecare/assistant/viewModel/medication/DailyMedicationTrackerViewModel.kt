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
import com.panashecare.assistant.model.objects.Intake
import com.panashecare.assistant.model.objects.Medication
import com.panashecare.assistant.model.objects.Prescription
import com.panashecare.assistant.model.objects.User
import com.panashecare.assistant.model.repository.DailyMedicationLogRepository
import com.panashecare.assistant.model.repository.MedicationRepository
import com.panashecare.assistant.model.repository.MedicationResult
import com.panashecare.assistant.model.repository.PrescriptionRepository
import com.panashecare.assistant.model.repository.PrescriptionResult
import com.panashecare.assistant.model.repository.UserRepository
import com.panashecare.assistant.model.service.LocalNotificationWorker
import com.panashecare.assistant.utils.TimeSerialisationHelper
import com.panashecare.assistant.viewModel.shiftManagement.SingleShiftState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val medicationRepository: MedicationRepository,
    private val userRepository: UserRepository,
    private val userId: String
) : ViewModel() {

    var state by mutableStateOf(DailyMedicationTrackerState())
        private set
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()
    private var prescriptionScheduleState =
        MutableStateFlow<PrescriptionResult>(PrescriptionResult.Loading)
    private val helper = TimeSerialisationHelper()

    private val medication = MutableStateFlow<Medication?>(null)
    private val medicationMap = mutableStateOf<Map<String, Medication>>(emptyMap())
    private var medicationList = MutableStateFlow<MedicationResult>(MedicationResult.Loading)

    init {
        // set current time of day state
        val currentTimeOfDay = getCurrentTimeOfDay()
        state = state.copy(currentTimeOfDay = currentTimeOfDay)

        // load user for authorisation checks
        loadUser(userId)

        // get patient daily prescriptions
        loadPrescriptionSchedule()

        // get medication information
        loadAllMedications()

        // load logs for today if any exist
        loadTodayLogs()

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

    private fun loadTodayLogs() {
        val today = helper.convertDateToString(
            LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )

        viewModelScope.launch {
            dailyMedicationLogRepository.getLogByDateId(today) { logs ->
                val morningLogs = logs?.morningIntake
                val afternoonLogs = logs?.afternoonIntake
                val eveningLogs = logs?.eveningIntake

                Log.d("Panashe Logs", "morning logs: $morningLogs")
                Log.d("Panashe Logs", "afternoon logs: $afternoonLogs")
                Log.d("Panashe Logs", "evening logs: $eveningLogs")

                state = state.copy(
                    isMorningFirstChecked = morningLogs?.getOrNull(0)?.wasTaken ?: false,
                    isMorningSecondChecked = morningLogs?.getOrNull(1)?.wasTaken ?: false,
                    isAfternoonFirstChecked = afternoonLogs?.getOrNull(0)?.wasTaken ?: false,
                    isAfternoonSecondChecked = afternoonLogs?.getOrNull(1)?.wasTaken ?: false,
                    isEveningFirstChecked = eveningLogs?.getOrNull(0)?.wasTaken ?: false,
                    isEveningSecondChecked = eveningLogs?.getOrNull(1)?.wasTaken ?: false
                )
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

            val intakeList = medications?.mapIndexed { index, item ->
                Intake(
                    medicationId = item.medication,
                    wasTaken = checks.getOrNull(index) ?: false
                )
            }?.filter { it.medicationId != null } ?: return@launch

            // Update Firebase log and inventory
            when (timeOfDay) {
                "morning" -> {
                    // Create a new log for the day
                    val log = DailyMedicationLog(
                        id = today,
                        date = today,
                        morningIntake = intakeList,
                    )
                    dailyMedicationLogRepository.submitLog(log) { success ->
                        if (success) Log.d("Firebase", "Morning log submitted")
                        else Log.e("Firebase", "Morning log failed")
                    }
                }
                "afternoon", "evening" -> {
                    // Update existing log
                    dailyMedicationLogRepository.updateLog(
                        id = today,
                        timeOfIntake = "${timeOfDay}Intake",
                        intake = intakeList
                    )
                }
            }

            // Update stock for taken meds
            intakeList.forEach { intake ->
                if (intake.wasTaken == true) {
                    val med = medicationMap.value[intake.medicationId]
                    val newStock = (med?.totalInStock ?: 1) - 1
                    medicationRepository.updateMedication(
                        medicationId = intake.medicationId ?: return@forEach,
                        newInventoryLevel = newStock
                    ) { success ->
                        if (success) Log.d("Firebase", "Inventory updated")
                        else Log.e("Firebase", "Inventory update failed")
                    }
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
            morningIntake = emptyList(),
            afternoonIntake = emptyList(),
            eveningIntake = emptyList()
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
    val user: User? = null,
    val currentTimeOfDay: String = "morning"
)

class DailyMedicationTrackerViewModelFactory(
    private val prescriptionRepository: PrescriptionRepository,
    private val dailyMedicationLogRepository: DailyMedicationLogRepository,
    private val medicationRepository: MedicationRepository,
    private val userRepository: UserRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DailyMedicationTrackerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DailyMedicationTrackerViewModel(
                prescriptionRepository,
                dailyMedicationLogRepository,
                medicationRepository,
                userRepository,
                userId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


