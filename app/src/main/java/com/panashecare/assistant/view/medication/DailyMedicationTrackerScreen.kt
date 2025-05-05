package com.panashecare.assistant.view.medication

import MedicationDetailsCard
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panashecare.assistant.components.HeaderButtonPair
import com.panashecare.assistant.model.objects.Medication
import com.panashecare.assistant.model.repository.DailyMedicationLogRepository
import com.panashecare.assistant.model.repository.MedicationRepository
import com.panashecare.assistant.model.repository.PrescriptionRepository
import com.panashecare.assistant.viewModel.medication.DailyMedicationTrackerState
import com.panashecare.assistant.viewModel.medication.DailyMedicationTrackerViewModel
import com.panashecare.assistant.viewModel.medication.DailyMedicationTrackerViewModelFactory

@Composable
fun DailyMedicationTrackerScreen(prescriptionRepository: PrescriptionRepository, dailyMedicationLogRepository: DailyMedicationLogRepository, medicationRepository: MedicationRepository){

    val viewModel = viewModel<DailyMedicationTrackerViewModel>(factory = DailyMedicationTrackerViewModelFactory(prescriptionRepository, dailyMedicationLogRepository, medicationRepository))

    DailyMedicationTracker(
        state = viewModel.state,
        onMorningFirstCheckedChange = viewModel::updateMorningFirstChecked,
        onMorningSecondCheckedChange = viewModel::updateMorningSecondChecked,
        onAfternoonFirstCheckedChange = viewModel::updateAfternoonFirstChecked,
        onAfternoonSecondCheckedChange = viewModel::updateAfternoonSecondChecked,
        onEveningFirstCheckedChange = viewModel::updateEveningFirstChecked,
        onEveningSecondCheckedChange = viewModel::updateEveningSecondChecked,
        onSubmitLog = viewModel::confirmMedicationIntake,
        getMedicationById = { id -> viewModel.getMedicationById(id) }
    )

}

@Composable
fun DailyMedicationTracker(
    modifier: Modifier = Modifier,
    state: DailyMedicationTrackerState,
    onSubmitLog: (String) -> Unit,
    onMorningFirstCheckedChange: (Boolean) -> Unit,
    onMorningSecondCheckedChange: (Boolean) -> Unit,
    onAfternoonFirstCheckedChange: (Boolean) -> Unit,
    onAfternoonSecondCheckedChange: (Boolean) -> Unit,
    onEveningFirstCheckedChange: (Boolean) -> Unit,
    onEveningSecondCheckedChange: (Boolean) -> Unit,
    getMedicationById: (String) -> Medication?

    ){
    Column(
        modifier = modifier.padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ){
        HeaderButtonPair(
            pageHeader = "Daily Medication Tracker",
            headerButton = "Submit Log",
            onNavigationClick = { onSubmitLog("evening") }
        )

        Text(
            text = "Morning ${state.prescriptions?.morningTime}",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight(400),
            )
        )

        if (state.prescriptions?.morningMedication?.isNotEmpty() == true) {
            Column(
                modifier = Modifier
                    .fillMaxWidth().fillMaxHeight(0.3f)
            ) {
                state.prescriptions.morningMedication.forEachIndexed { index, _ ->
                    MedicationDetailsCard(
                        medicalList = state.prescriptions.morningMedication,
                        isChecked = if (index == 0) state.isMorningFirstChecked else state.isMorningSecondChecked,
                        onCheckedChange = if (index == 0) onMorningFirstCheckedChange else onMorningSecondCheckedChange,
                        index = index,
                        getMedicationById = getMedicationById
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No prescriptions listed")
            }
        }

        Text(
            text = "Afternoon ${state.prescriptions?.afternoonTime}",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight(400),
            )
        )

        if (state.prescriptions?.afternoonMedication?.isNotEmpty() == true) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
            ) {
                state.prescriptions.afternoonMedication.forEachIndexed { index, _ ->
                    MedicationDetailsCard(
                        medicalList = state.prescriptions.afternoonMedication,
                        isChecked = if (index == 0) state.isAfternoonFirstChecked else state.isAfternoonSecondChecked,
                        onCheckedChange = if (index == 0) onAfternoonFirstCheckedChange else onAfternoonSecondCheckedChange,
                        index = index,
                        getMedicationById = getMedicationById
                    )

                }
            }
        } else {
            // Use weight to center the text properly
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No prescriptions listed")
            }
        }

        Text(
            text = "Nighty ${state.prescriptions?.eveningTime}",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight(400),
            )
        )

        if (state.prescriptions?.eveningMedication?.isNotEmpty() == true) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
            ) {
                state.prescriptions.eveningMedication.forEachIndexed { index, _ ->

                        MedicationDetailsCard(
                            medicalList = state.prescriptions.eveningMedication,
                            isChecked = if (index == 0) state.isEveningFirstChecked else state.isEveningSecondChecked,
                            onCheckedChange = if (index == 0) onEveningFirstCheckedChange else onEveningSecondCheckedChange,
                            index = index,
                            getMedicationById = getMedicationById
                        )

                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxHeight(0.3f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No prescriptions listed")
            }
        }

    }
}

@Preview
@Composable
fun PreviewTracker(){
    DailyMedicationTrackerScreen(PrescriptionRepository(), DailyMedicationLogRepository(), MedicationRepository())
}