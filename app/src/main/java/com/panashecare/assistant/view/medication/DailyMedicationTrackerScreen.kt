package com.panashecare.assistant.view.medication

import MedicationDetailsCard
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.panashecare.assistant.components.HeaderSingle
import com.panashecare.assistant.components.ShiftCard
import com.panashecare.assistant.model.repository.PrescriptionRepository
import com.panashecare.assistant.viewModel.medication.DailyMedicationTrackerState
import com.panashecare.assistant.viewModel.medication.DailyMedicationTrackerViewModel
import com.panashecare.assistant.viewModel.medication.DailyMedicationTrackerViewModelFactory

@Composable
fun DailyMedicationTrackerScreen(prescriptionRepository: PrescriptionRepository){

    val viewModel = viewModel<DailyMedicationTrackerViewModel>(factory = DailyMedicationTrackerViewModelFactory(prescriptionRepository))

    DailyMedicationTracker(
        state = viewModel.state,
        onMorningFirstCheckedChange = viewModel::updateMorningFirstChecked,
        onMorningSecondCheckedChange = viewModel::updateMorningSecondChecked,
        onAfternoonFirstCheckedChange = viewModel::updateAfternoonFirstChecked,
        onAfternoonSecondCheckedChange = viewModel::updateAfternoonSecondChecked,
        onEveningFirstCheckedChange = viewModel::updateEveningFirstChecked,
        onEveningSecondCheckedChange = viewModel::updateEveningSecondChecked,
    )

}

@Composable
fun DailyMedicationTracker(
    modifier: Modifier = Modifier,
    state: DailyMedicationTrackerState,
    onMorningFirstCheckedChange: (Boolean) -> Unit,
    onMorningSecondCheckedChange: (Boolean) -> Unit,
    onAfternoonFirstCheckedChange: (Boolean) -> Unit,
    onAfternoonSecondCheckedChange: (Boolean) -> Unit,
    onEveningFirstCheckedChange: (Boolean) -> Unit,
    onEveningSecondCheckedChange: (Boolean) -> Unit,

    ){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ){
        HeaderSingle("Daily Medication Tracker")

        Text(
            text = "Morning 10AM",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight(400),
            )
        )

        Log.d("Panashe Meds", "DailyMedicationTracker: ${state.morningPrescriptions}")

        if (state.morningPrescriptions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 12.dp).fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(vertical = 12.dp),
            ) {
                itemsIndexed(
                    items = state.morningPrescriptions,
                   // key = { index, prescription}
                ) { index, _ ->
                    MedicationDetailsCard(
                        medicalList = state.morningPrescriptions,
                        isChecked = if (index == 0) state.isMorningFirstChecked else state.isMorningSecondChecked,
                        onCheckedChange = if (index == 0) onMorningFirstCheckedChange else onMorningSecondCheckedChange,
                        index = index
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
            text = "Afternoon 12PM",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight(400),
            )
        )

        if (state.afternoonPrescriptions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 12.dp).fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(vertical = 12.dp),
            ) {
                itemsIndexed(
                    items = state.afternoonPrescriptions,
                  //  key = { index, prescription -> prescription.id!! }
                ) { index, _ ->
                    MedicationDetailsCard(
                            medicalList = state.afternoonPrescriptions,
                            isChecked = if (index == 0) state.isAfternoonFirstChecked else state.isAfternoonSecondChecked,
                            onCheckedChange = if (index == 0) onAfternoonFirstCheckedChange else onAfternoonSecondCheckedChange,
                            index = index
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
            text = "Nighty 10AM",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight(400),
            )
        )

        if (state.eveningPrescriptions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 12.dp).fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(vertical = 12.dp),
            ) {
                itemsIndexed(
                    items = state.eveningPrescriptions,
                  //  key = { index, prescription -> prescription.id!! }
                ) { index, _ ->

                        MedicationDetailsCard(
                            medicalList = state.eveningPrescriptions,
                            isChecked = if (index == 0) state.isEveningFirstChecked else state.isEveningSecondChecked,
                            onCheckedChange = if (index == 0) onEveningFirstCheckedChange else onEveningSecondCheckedChange,
                            index = index
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

    }
}

@Preview
@Composable
fun PreviewTracker(){
    DailyMedicationTrackerScreen(PrescriptionRepository())
}