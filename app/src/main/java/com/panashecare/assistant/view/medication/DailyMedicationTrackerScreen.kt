package com.panashecare.assistant.view.medication

import MedicationDetailsCard
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panashecare.assistant.AppColors
import com.panashecare.assistant.R
import com.panashecare.assistant.components.HeaderButtonPair
import com.panashecare.assistant.model.objects.Medication
import com.panashecare.assistant.model.objects.MedicationWithDosage
import com.panashecare.assistant.model.repository.DailyMedicationLogRepository
import com.panashecare.assistant.model.repository.MedicationRepository
import com.panashecare.assistant.model.repository.PrescriptionRepository
import com.panashecare.assistant.viewModel.medication.DailyMedicationTrackerState
import com.panashecare.assistant.viewModel.medication.DailyMedicationTrackerViewModel
import com.panashecare.assistant.viewModel.medication.DailyMedicationTrackerViewModelFactory

@Composable
fun DailyMedicationTrackerScreen(prescriptionRepository: PrescriptionRepository, dailyMedicationLogRepository: DailyMedicationLogRepository, medicationRepository: MedicationRepository, navigateToStockManagement: ()-> Unit){

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
        getMedicationById = { id -> viewModel.getMedicationById(id) },
        navigateToStockManagement = navigateToStockManagement
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
    getMedicationById: (String) -> Medication?,
    navigateToStockManagement: ()-> Unit

    ){

    val appColors = AppColors()

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

        if(true /*todo replace with admin check*/){
            Row(
                modifier = modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .height(71.dp)
                    .background(color = Color(0xFFE7F7FA), shape = RoundedCornerShape(18.dp))
                    .padding(horizontal = 13.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = modifier
                        .padding(end = 10.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(25.dp)
                        )
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.bxs_first_aid),
                        contentDescription = null
                    )
                }

                Text(
                    text = "Would you like to update\nyour stock?",
                    style = TextStyle(
                        fontSize = 13.sp,
                    )
                )
                Spacer(modifier = modifier.weight(1f))

                Button(
                    onClick = {
                        navigateToStockManagement()
                    },
                    modifier = Modifier
                        .width(190.dp)
                        .height(45.dp),
                    shape = RoundedCornerShape(size = 47.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appColors.primaryDark,
                        contentColor = Color.White
                    )
                ) {
                    Text("Update", fontSize = 16.sp, fontWeight = FontWeight(400))
                }
            }
        }


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
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f)
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
    //DailyMedicationTrackerScreen(PrescriptionRepository(), DailyMedicationLogRepository(), MedicationRepository())
}