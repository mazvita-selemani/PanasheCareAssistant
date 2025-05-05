package com.panashecare.assistant.viewModel.medication

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.panashecare.assistant.model.objects.DailyMedicationLog
import com.panashecare.assistant.model.objects.Medication
import com.panashecare.assistant.model.objects.Prescription
import com.panashecare.assistant.model.repository.DailyMedicationLogRepository
import com.panashecare.assistant.model.repository.MedicationRepository
import com.panashecare.assistant.model.repository.MedicationResult
import com.panashecare.assistant.model.repository.PrescriptionRepository
import com.panashecare.assistant.model.repository.PrescriptionResult
import com.panashecare.assistant.utils.TimeSerialisationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

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
            var log: DailyMedicationLog
            var logSecond: DailyMedicationLog
            val today = helper.convertDateToString(
                LocalDate
                    .now()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            )

            if (timeOfDay == "morning") {

                log = DailyMedicationLog(
                    date = today,
                    medicationId = state.prescriptions?.morningMedication?.get(0)?.medication,
                    wasTaken = state.isMorningFirstChecked,
                    timeOfDay = TimeOfDay.MORNING.name
                )

                dailyMedicationLogRepository.submitLog(
                    log = log,
                ) { success ->
                    if (success) {
                        Log.d("Firebase", "Prescription schedule created!")

                    } else {
                        Log.e("Firebase", "Prescription schedule creation failed.")
                    }
                }

                // update medication levels in medication document
                /*state.prescriptions?.morningMedication?.get(0)?.medication?.let {
                    medicationRepository.updateMedication(
                        medicationId = it,
                        newInventoryLevel = state.prescriptions?.morningMedication?.get(0)?.medication.totalInStock?.minus(1) ?: 0
                    ){ success ->
                        if (success) {
                            Log.d("Firebase", "Inventory Level updated!")

                        } else {
                            Log.e("Firebase", "Inventory Level update failed.")
                        }
                    }
                }*/

                if ((state.prescriptions?.morningMedication?.size ?: 0) > 1) {
                    logSecond = DailyMedicationLog(
                        date = today,
                        medicationId = state.prescriptions?.morningMedication?.get(1)?.medication,
                        wasTaken = state.isMorningSecondChecked,
                        timeOfDay = TimeOfDay.MORNING.name
                    )

                    dailyMedicationLogRepository.submitLog(
                        log = logSecond,
                    ) { success ->
                        if (success) {
                            Log.d("Firebase", "Prescription schedule created!")

                        } else {
                            Log.e("Firebase", "Prescription schedule creation failed.")
                        }
                    }

                    /*state.prescriptions?.morningMedication?.get(1)?.medication?.let {
                        medicationRepository.updateMedication(
                            medicationId = it,
                            newInventoryLevel = state.prescriptions?.morningMedication?.get(1)?.medication?.totalInStock?.minus(
                                1
                            ) ?: 0
                        ){ success ->
                            if (success) {
                                Log.d("Firebase", "Inventory Level updated!")

                            } else {
                                Log.e("Firebase", "Inventory Level update failed.")
                            }
                        }
                    }*/
                }


            }

            if (timeOfDay == "afternoon") {
                log = DailyMedicationLog(
                    date = today,
                    medicationId = state.prescriptions?.afternoonMedication?.get(0)?.medication,
                    wasTaken = state.isAfternoonFirstChecked,
                    timeOfDay = TimeOfDay.AFTERNOON.name
                )

                dailyMedicationLogRepository.submitLog(
                    log = log,
                ) { success ->
                    if (success) {
                        Log.d("Firebase", "Prescription schedule created!")

                    } else {
                        Log.e("Firebase", "Prescription schedule creation failed.")
                    }
                }

                // update medication levels in medication document
                /*state.prescriptions?.afternoonMedication?.get(0)?.medication?.let {
                    medicationRepository.updateMedication(
                        medicationId = it,
                        newInventoryLevel = state.prescriptions?.afternoonMedication?.get(0)?.medication?.totalInStock?.minus(
                            1
                        ) ?: 0
                    ){ success ->
                        if (success) {
                            Log.d("Firebase", "Inventory Level updated!")

                        } else {
                            Log.e("Firebase", "Inventory Level update failed.")
                        }
                    }
                }*/

                if ((state.prescriptions?.afternoonMedication?.size ?: 0) > 1) {
                    logSecond = DailyMedicationLog(
                        date = today,
                        medicationId = state.prescriptions?.afternoonMedication?.get(1)?.medication,
                        wasTaken = state.isAfternoonSecondChecked,
                        timeOfDay = TimeOfDay.AFTERNOON.name
                    )

                    dailyMedicationLogRepository.submitLog(
                        log = logSecond,
                    ) { success ->
                        if (success) {
                            Log.d("Firebase", "Prescription schedule created!")

                        } else {
                            Log.e("Firebase", "Prescription schedule creation failed.")
                        }
                    }

                    // update medication levels in medication document
                    /*state.prescriptions?.afternoonMedication?.get(1)?.medication?.let {
                        medicationRepository.updateMedication(
                            medicationId = it,
                            newInventoryLevel = state.prescriptions?.afternoonMedication?.get(1)?.medication?.totalInStock?.minus(
                                1
                            ) ?: 0
                        ){ success ->
                            if (success) {
                                Log.d("Firebase", "Inventory Level updated!")

                            } else {
                                Log.e("Firebase", "Inventory Level update failed.")
                            }
                        }
                    }*/
                }

            }

            if (timeOfDay == "evening") {
                log = DailyMedicationLog(
                    date = today,
                    medicationId = state.prescriptions?.eveningMedication?.get(0)?.medication,
                    wasTaken = state.isEveningFirstChecked,
                    timeOfDay = TimeOfDay.EVENING.name
                )

                dailyMedicationLogRepository.submitLog(
                    log = log,
                ) { success ->
                    if (success) {
                        Log.d("Firebase", "Prescription schedule created!")

                    } else {
                        Log.e("Firebase", "Prescription schedule creation failed.")
                    }
                }

                // update medication levels in medication document
                /*state.prescriptions?.eveningMedication?.get(0)?.medication?.let {
                    medicationRepository.updateMedication(
                        medicationId = it,
                        newInventoryLevel = state.prescriptions?.afternoonMedication?.get(0)?.medication?.totalInStock?.minus(
                            1
                        ) ?: 0
                    ){ success ->
                        if (success) {
                            Log.d("Firebase", "Inventory Level updated!")

                        } else {
                            Log.e("Firebase", "Inventory Level update failed.")
                        }
                    }
                }*/

                if ((state.prescriptions?.eveningMedication?.size ?: 0) > 1) {
                    logSecond = DailyMedicationLog(
                        date = today,
                        medicationId = state.prescriptions?.eveningMedication?.get(1)?.medication,
                        wasTaken = state.isEveningSecondChecked,
                        timeOfDay = TimeOfDay.EVENING.name
                    )

                    dailyMedicationLogRepository.submitLog(
                        log = logSecond,
                    ) { success ->
                        if (success) {
                            Log.d("Firebase", "Prescription schedule created!")

                        } else {
                            Log.e("Firebase", "Prescription schedule creation failed.")
                        }
                    }

                    // update medication levels in medication document
                    /*state.prescriptions?.eveningMedication?.get(1)?.medication?.let {
                        medicationRepository.updateMedication(
                            medicationId = it,
                            newInventoryLevel = state.prescriptions?.eveningMedication?.get(1)?.medication?.totalInStock?.minus(
                                1
                            ) ?: 0
                        ){ success ->
                            if (success) {
                                Log.d("Firebase", "Inventory Level updated!")

                            } else {
                                Log.e("Firebase", "Inventory Level update failed.")
                            }
                        }
                    }*/
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
    val prescriptions: Prescription? = null,

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


