package com.panashecare.assistant.view.vitals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panashecare.assistant.AppColors
import com.panashecare.assistant.components.FormField
import com.panashecare.assistant.components.HeaderButtonPair
import com.panashecare.assistant.model.objects.Vitals
import com.panashecare.assistant.model.repository.VitalsRepository
import com.panashecare.assistant.utils.TimeSerialisationHelper
import com.panashecare.assistant.view.shiftManagement.CustomSpacer
import com.panashecare.assistant.viewModel.vitals.LogVitalsState
import com.panashecare.assistant.viewModel.vitals.LogVitalsViewModel
import com.panashecare.assistant.viewModel.vitals.LogVitalsViewModelFactory
import java.time.LocalDate
import java.time.ZoneId


@Composable
fun LogVitalsScreen(modifier: Modifier = Modifier, vitalsRepository: VitalsRepository, navigateToVitalsList: () -> Unit) {

    val viewModel = viewModel<LogVitalsViewModel>(factory = LogVitalsViewModelFactory(vitalsRepository))

    val state = viewModel.state
    val helper = TimeSerialisationHelper()
    val today =  helper.convertDateToString(LocalDate
        .now()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli())

    val vitals = Vitals(
        loggerId = "1234",
        adminId = "1234",
        oxygenSaturationRecord = state.oxygenSaturationRecord ?: "",
        heartRateRecord = state.heartRateRecord ?: "",
        bloodPressureRecord = state.bloodPressureRecord?: "",
        dateOfRecording = today
    )

    LogVitals(
        modifier = modifier,
        state = state,
        updateOxygenSaturationRecord = viewModel::updateOxygenSaturationRecord,
        updateBloodPressureRecord = viewModel::updateBloodPressureRecord,
        updateHeartRateRecord = viewModel::updateHeartRate,
        submitLog = { viewModel.submitLog(vitals) },
        navigateToVitalsList = navigateToVitalsList
    )
}

@Composable
fun LogVitals(
    modifier: Modifier = Modifier,
    state: LogVitalsState,
    updateOxygenSaturationRecord: (String) -> Unit,
    updateBloodPressureRecord: (String) -> Unit,
    updateHeartRateRecord: (String) -> Unit,
    submitLog: () -> Unit,
    navigateToVitalsList: () -> Unit
) {
    val appColors = AppColors()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        HeaderButtonPair("Update Shift", "Help?", {})

        CustomSpacer(10)

        Column(
            modifier = Modifier
                .align(Alignment.Start)
                .fillMaxWidth()
                .fillMaxHeight(.5f)
                .background(
                    color = appColors.surface,
                    shape = RoundedCornerShape(size = 18.dp)
                )
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Text(
                text = "Heart Rate",
                style = TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(700),
                    color = appColors.formTextPrimary,
                )
            )

            FormField(
                value = state.heartRateRecord ?: "",
                onChange = { updateHeartRateRecord(it) },
                modifier = Modifier,
                label = "",
                placeholder = "",
                horizontalPadding = 0
            )


            CustomSpacer(5)

            Text(
                text = "Oxygen Saturation (SpO2)",
                style = TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(700),
                    color = appColors.formTextPrimary,
                )
            )

            FormField(
                value = state.oxygenSaturationRecord ?: "",
                onChange = { updateOxygenSaturationRecord(it) },
                modifier = Modifier,
                label = "",
                placeholder = "",
                horizontalPadding = 0
            )

            CustomSpacer(5)

            Text(
                text = "Blood Pressure (BP)",
                style = TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(700),
                    color = appColors.formTextPrimary,
                )
            )

            FormField(
                value = state.bloodPressureRecord ?: "",
                onChange = { updateBloodPressureRecord(it) },
                modifier = Modifier,
                label = "",
                placeholder = "",
                horizontalPadding = 0
            )

        }

        CustomSpacer(25)

        // Notes section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(color = appColors.surface, shape = RoundedCornerShape(size = 20.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Notes",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight(500),
                    color = appColors.formTextPrimary,
                )
            )

            CustomSpacer(5)

            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("(Optional)") },
                modifier = modifier
                    .fillMaxHeight(0.8f)
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = appColors.formTextPrimary,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }

        CustomSpacer(60)


        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Button(
                onClick = {
                    submitLog()
                    navigateToVitalsList()
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
                Text("Submit Recording", fontSize = 16.sp, fontWeight = FontWeight(400))
            }
        }

    }
}


@Preview
@Composable
fun PreviewLogVitals() {
    LogVitalsScreen(
        navigateToVitalsList = {},
        vitalsRepository = VitalsRepository()
    )
}