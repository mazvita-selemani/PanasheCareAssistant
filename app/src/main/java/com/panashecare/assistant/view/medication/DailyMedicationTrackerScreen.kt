package com.panashecare.assistant.view.medication

import MedicationDetailsCard
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panashecare.assistant.AppColors
import com.panashecare.assistant.R
import com.panashecare.assistant.access.AccessControl
import com.panashecare.assistant.access.Permission
import com.panashecare.assistant.components.HeaderButtonPair
import com.panashecare.assistant.model.objects.Medication
import com.panashecare.assistant.model.objects.User
import com.panashecare.assistant.model.repository.DailyMedicationLogRepository
import com.panashecare.assistant.model.repository.MedicationRepository
import com.panashecare.assistant.model.repository.PrescriptionRepository
import com.panashecare.assistant.model.repository.UserRepository
import com.panashecare.assistant.view.shiftManagement.CustomSpacer
import com.panashecare.assistant.viewModel.medication.DailyMedicationTrackerState
import com.panashecare.assistant.viewModel.medication.DailyMedicationTrackerViewModel
import com.panashecare.assistant.viewModel.medication.DailyMedicationTrackerViewModelFactory

@Composable
fun DailyMedicationTrackerScreen(
    modifier: Modifier = Modifier,
    prescriptionRepository: PrescriptionRepository,
    dailyMedicationLogRepository: DailyMedicationLogRepository,
    medicationRepository: MedicationRepository,
    userRepository: UserRepository,
    userId: String,
    navigateToStockManagement: () -> Unit
) {

    val viewModel = viewModel<DailyMedicationTrackerViewModel>(
        factory = DailyMedicationTrackerViewModelFactory(
            prescriptionRepository,
            dailyMedicationLogRepository,
            medicationRepository,
            userRepository,
            userId
        )
    )

    DailyMedicationTracker(
        modifier = modifier,
        state = viewModel.state,
        onMorningFirstCheckedChange = viewModel::updateMorningFirstChecked,
        onMorningSecondCheckedChange = viewModel::updateMorningSecondChecked,
        onAfternoonFirstCheckedChange = viewModel::updateAfternoonFirstChecked,
        onAfternoonSecondCheckedChange = viewModel::updateAfternoonSecondChecked,
        onEveningFirstCheckedChange = viewModel::updateEveningFirstChecked,
        onEveningSecondCheckedChange = viewModel::updateEveningSecondChecked,
        onSubmitLog = viewModel::confirmMedicationIntake,
        getMedicationById = { id -> viewModel.getMedicationById(id) },
        navigateToStockManagement = navigateToStockManagement,
        user = viewModel.user.value ?: User(firstName = "Loading...")
    )

}

@Composable
fun DailyMedicationTracker(
    modifier: Modifier = Modifier,
    state: DailyMedicationTrackerState,
    onSubmitLog: (String) -> Unit,
    user: User,
    onMorningFirstCheckedChange: (Boolean) -> Unit,
    onMorningSecondCheckedChange: (Boolean) -> Unit,
    onAfternoonFirstCheckedChange: (Boolean) -> Unit,
    onAfternoonSecondCheckedChange: (Boolean) -> Unit,
    onEveningFirstCheckedChange: (Boolean) -> Unit,
    onEveningSecondCheckedChange: (Boolean) -> Unit,
    getMedicationById: (String) -> Medication?,
    navigateToStockManagement: () -> Unit

) {

    val appColors = AppColors()
    val isMorningEnabled = state.currentTimeOfDay == "morning"
    val isAfternoonEnabled = state.currentTimeOfDay == "afternoon"
    val isEveningEnabled = state.currentTimeOfDay == "evening"


    Column(
        modifier = modifier.padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        HeaderButtonPair(
            pageHeader = "Daily Medication Tracker",
            headerButton = "Submit Log",
            onNavigationClick = { onSubmitLog(state.currentTimeOfDay) },
            width = 140
        )

        CustomSpacer(10)

        AccessControl.WithPermission(
            user = user,
            permission = Permission.UpdateInventory,
            onAuthorized = {
                Row(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxWidth()
                        .height(71.dp)
                        .background(color = Color(0xFFE7F7FA), shape = RoundedCornerShape(18.dp))
                        .padding(horizontal = 13.dp, vertical = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
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
                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            navigateToStockManagement()
                        },
                        modifier = Modifier
                            .width(150.dp)
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
            })


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text("Morning ${state.prescriptions?.morningTime}", fontSize = 20.sp)
            }
            if (state.prescriptions?.morningMedication?.isNotEmpty() == true) {
                itemsIndexed(state.prescriptions.morningMedication) { index, _ ->
                    MedicationDetailsCard(
                        medicalList = state.prescriptions.morningMedication,
                        isChecked = if (index == 0 && isMorningEnabled) state.isMorningFirstChecked else state.isMorningSecondChecked,
                        onCheckedChange = if (index == 0 && isMorningEnabled) onMorningFirstCheckedChange else onMorningSecondCheckedChange,
                        index = index,
                        getMedicationById = getMedicationById,
                        enabled = isMorningEnabled
                    )
                }
            } else {
                item {
                    Text("No prescriptions listed")
                }
            }

            item {
                Text("Afternoon ${state.prescriptions?.afternoonTime}", fontSize = 20.sp)
            }
            if (state.prescriptions?.afternoonMedication?.isNotEmpty() == true) {
                itemsIndexed(state.prescriptions.afternoonMedication) { index, _ ->
                    MedicationDetailsCard(
                        medicalList = state.prescriptions.afternoonMedication,
                        isChecked = if (index == 0 && isAfternoonEnabled) state.isAfternoonFirstChecked else state.isAfternoonSecondChecked,
                        onCheckedChange = if (index == 0 && isAfternoonEnabled) onAfternoonFirstCheckedChange else onAfternoonSecondCheckedChange,
                        index = index,
                        getMedicationById = getMedicationById,
                        enabled = isAfternoonEnabled
                    )
                }
            } else {
                item {
                    Text("No prescriptions listed")
                }
            }

            item {
                Text("Night ${state.prescriptions?.eveningTime}", fontSize = 20.sp)
            }
            if (state.prescriptions?.eveningMedication?.isNotEmpty() == true) {
                itemsIndexed(state.prescriptions.eveningMedication) { index, _ ->
                    MedicationDetailsCard(
                        medicalList = state.prescriptions.eveningMedication,
                        isChecked = if (index == 0 && isEveningEnabled) state.isEveningFirstChecked else state.isEveningSecondChecked,
                        onCheckedChange = if (index == 0 && isEveningEnabled) onEveningFirstCheckedChange else onEveningSecondCheckedChange,
                        index = index,
                        getMedicationById = getMedicationById,
                        enabled = isEveningEnabled
                    )
                }
            } else {
                item {
                    Text("No prescriptions listed")
                }
            }
        }

    }
}

@Preview
@Composable
fun PreviewTracker() {
    DailyMedicationTrackerScreen(
        Modifier,
        PrescriptionRepository(),
        DailyMedicationLogRepository(),
        MedicationRepository(),
        userId = "3",
        navigateToStockManagement = { },
        userRepository = UserRepository()
    )
}