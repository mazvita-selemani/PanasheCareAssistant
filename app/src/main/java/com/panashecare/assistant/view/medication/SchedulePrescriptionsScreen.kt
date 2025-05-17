package com.panashecare.assistant.view.medication

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panashecare.assistant.AppColors
import com.panashecare.assistant.R
import com.panashecare.assistant.components.HeaderButtonPair
import com.panashecare.assistant.model.objects.Medication
import com.panashecare.assistant.model.objects.MedicationWithDosage
import com.panashecare.assistant.model.objects.Prescription
import com.panashecare.assistant.model.repository.MedicationRepository
import com.panashecare.assistant.model.repository.PrescriptionRepository
import com.panashecare.assistant.view.shiftManagement.CustomSpacer
import com.panashecare.assistant.viewModel.medication.PrescriptionDetailState
import com.panashecare.assistant.viewModel.medication.PrescriptionTimeState
import com.panashecare.assistant.viewModel.medication.PrescriptionViewModel
import com.panashecare.assistant.viewModel.medication.PrescriptionViewModelFactory
import com.panashecare.assistant.viewModel.medication.TimeOfDay

// form row indices
const val firstRow = 0
const val secondRow = 1

@Composable
fun SchedulePrescriptionsScreen(
    modifier: Modifier = Modifier,
    prescriptionRepository: PrescriptionRepository,
    medicationRepository: MedicationRepository,
    navigateToDailyMedicationTracker: () -> Unit
) {

    val viewModel = viewModel<PrescriptionViewModel>(
        factory = PrescriptionViewModelFactory(
            prescriptionRepository = prescriptionRepository,
            medicationRepository = medicationRepository
        )
    )

    val screenState = viewModel.screenState.collectAsState()
    val appColors = AppColors()

    val morningState = screenState.value.morning
    val afternoonState = screenState.value.afternoon
    val eveningState = screenState.value.evening

    var prescription: Prescription? = null

    if (morningState.prescriptionDetails.size ==2 && afternoonState.prescriptionDetails.size ==2 && eveningState.prescriptionDetails.size ==2) {
        prescription = Prescription(
            patientId = "1234",
            morningTime = morningState.time,
            morningMedication = listOf(
                MedicationWithDosage(morningState.prescriptionDetails[firstRow].selectedMedication.id, morningState.prescriptionDetails[firstRow].dosage),
                MedicationWithDosage(morningState.prescriptionDetails[secondRow].selectedMedication.id, morningState.prescriptionDetails[secondRow].dosage)
            ),
            afternoonTime = afternoonState.time,
            afternoonMedication = listOf(
                MedicationWithDosage(afternoonState.prescriptionDetails[firstRow].selectedMedication.id, afternoonState.prescriptionDetails[firstRow].dosage),
                MedicationWithDosage(afternoonState.prescriptionDetails[secondRow].selectedMedication.id, afternoonState.prescriptionDetails[secondRow].dosage)
            ),
            eveningTime = eveningState.time,
            eveningMedication = listOf(
                MedicationWithDosage(eveningState.prescriptionDetails[firstRow].selectedMedication.id, eveningState.prescriptionDetails[firstRow].dosage),
                MedicationWithDosage(eveningState.prescriptionDetails[secondRow].selectedMedication.id, eveningState.prescriptionDetails[secondRow].dosage)
            )
        )
    }

   SchedulePrescriptions(
       modifier = modifier,
       morningState = morningState,
       afternoonState = afternoonState,
       eveningState = eveningState,
       onValueChanged = viewModel::onTimeChanged,
       onDosageChange = viewModel::onDosageChanged,
       appColors = appColors,
       onMedicationExpandedChange = viewModel::onMedicationExpandedChange,
       onUnitsExpandedChange = viewModel::onUnitsExpandedChange,
       onSelectMedicationChange = viewModel::onSelectMedication,
       onSelectUnitsChange = viewModel::onSelectUnits,
       onAddPrescriptionRow = viewModel::onAddPrescriptionRow,
       createPrescription = {
           if (viewModel.validateFields()) {
               if (prescription != null) {
                   viewModel.createPrescriptionSchedule(prescription)
                   navigateToDailyMedicationTracker()
               }
           }
       },
       errors = screenState.value.errors,
   )

}

@Composable
fun SchedulePrescriptions(
    modifier: Modifier = Modifier,
    createPrescription: () -> Unit,
    morningState: PrescriptionTimeState,
    afternoonState: PrescriptionTimeState,
    eveningState: PrescriptionTimeState,
    onValueChanged: (TimeOfDay, String) -> Unit,
    onDosageChange: (TimeOfDay, Int, Int) -> Unit,
    appColors: AppColors,
    onMedicationExpandedChange: (TimeOfDay, Int, Boolean) -> Unit,
    onUnitsExpandedChange: (TimeOfDay, Int, Boolean) -> Unit,
    onSelectMedicationChange: (TimeOfDay, Int, Medication) -> Unit,
    onSelectUnitsChange: (TimeOfDay, Int, String) -> Unit,
    onAddPrescriptionRow: (TimeOfDay) -> Unit,
    errors: Map<String, String>
) {


    Column {
        HeaderButtonPair(
            pageHeader = "Schedule Prescriptions",
            headerButton = "Confirm",
            onNavigationClick = {
                createPrescription()
            }
        )

        Column(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(500.dp, 650.dp)
                .verticalScroll(rememberScrollState())
                .background(color = Color(0xFFF4F4F5), shape = RoundedCornerShape(size = 18.dp))
                .padding(16.dp)
        ) {
            PrescriptionsInput(
                appColors = appColors,
                onMedicationExpandedChange = onMedicationExpandedChange,
                onUnitsExpandedChange = onUnitsExpandedChange,
                onSelectMedicationChange = onSelectMedicationChange,
                onSelectUnitsChange = onSelectUnitsChange,
                timeOfDay = TimeOfDay.MORNING,
                time = morningState.time ?: "",
                onValueChanged = onValueChanged,
                onDosageChange = onDosageChange,
                onAddPrescriptionRow = onAddPrescriptionRow,
                prescriptionDetailState = morningState.prescriptionDetails,
                errors = errors
            )

            CustomSpacer(15)

            PrescriptionsInput(
                appColors = appColors,
                onMedicationExpandedChange = onMedicationExpandedChange,
                onUnitsExpandedChange = onUnitsExpandedChange,
                onSelectMedicationChange = onSelectMedicationChange,
                onSelectUnitsChange = onSelectUnitsChange,
                timeOfDay = TimeOfDay.AFTERNOON,
                time = afternoonState.time ?: "",
                onValueChanged = onValueChanged,
                onDosageChange = onDosageChange,
                onAddPrescriptionRow = onAddPrescriptionRow,
                prescriptionDetailState = afternoonState.prescriptionDetails,
                errors = errors
            )

            CustomSpacer(15)

            PrescriptionsInput(
                appColors = appColors,
                onMedicationExpandedChange = onMedicationExpandedChange,
                onUnitsExpandedChange = onUnitsExpandedChange,
                onSelectMedicationChange = onSelectMedicationChange,
                onSelectUnitsChange = onSelectUnitsChange,
                timeOfDay = TimeOfDay.EVENING,
                time = eveningState.time ?: "",
                onValueChanged = onValueChanged,
                onDosageChange = onDosageChange,
                onAddPrescriptionRow = onAddPrescriptionRow,
                prescriptionDetailState = eveningState.prescriptionDetails,
                errors = errors
            )

        }
    }


}


@Composable
fun PrescriptionsInput(
    modifier: Modifier = Modifier,
    prescriptionDetailState: MutableList<PrescriptionDetailState>,
    time: String,
    timeOfDay: TimeOfDay,
    onValueChanged: (TimeOfDay, String) -> Unit,
    onDosageChange: (TimeOfDay, Int, Int) -> Unit,
    appColors: AppColors,
    onMedicationExpandedChange: (TimeOfDay, Int, Boolean) -> Unit,
    onUnitsExpandedChange: (TimeOfDay, Int, Boolean) -> Unit,
    onSelectMedicationChange: (TimeOfDay, Int, Medication) -> Unit,
    onSelectUnitsChange: (TimeOfDay, Int, String) -> Unit,
    onAddPrescriptionRow: (TimeOfDay) -> Unit,
    errors: Map<String, String> // Receive the errors map
) {
    var isSecondRowVisible by remember { mutableStateOf(prescriptionDetailState.size > 1) } // Initialize based on the list size

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = if (errors.containsKey("${timeOfDay.name.lowercase()}Time")) Color.Red else Color.Black,
        unfocusedBorderColor = if (errors.containsKey("${timeOfDay.name.lowercase()}Time")) Color.Red else Color.Black,
        focusedTextColor = Color.Black
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Text(timeOfDay.name, fontWeight = FontWeight.Bold, color = Color.Gray)

        OutlinedTextField(
            value = time,
            onValueChange = { newTime -> onValueChanged(timeOfDay, newTime) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = textFieldColors,
            shape = RoundedCornerShape(15.dp),
            trailingIcon = {
                Icon(
                    modifier = Modifier.size(15.dp),
                    painter = painterResource(R.drawable.clock),
                    contentDescription = "Clock icon"
                )
            }
        )
        if (errors.containsKey("${timeOfDay.name.lowercase()}Time")) {
            Text(
                text = errors["${timeOfDay.name.lowercase()}Time"]!!,
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        MedicationRowWithError(
            time = time,
            dosage = prescriptionDetailState[firstRow].dosage,
            onDosageChange = { newDosage -> onDosageChange(timeOfDay, firstRow, newDosage) },
            isMedicationExpanded = prescriptionDetailState[firstRow].isMedicationExpanded,
            onMedicationExpandedChange = {
                onMedicationExpandedChange(
                    timeOfDay,
                    firstRow,
                    !prescriptionDetailState[firstRow].isMedicationExpanded
                )
            },
            isUnitsExpanded = prescriptionDetailState[firstRow].isUnitsExpanded,
            onUnitsExpandedChange = {
                onUnitsExpandedChange(
                    timeOfDay,
                    firstRow,
                    !prescriptionDetailState[firstRow].isUnitsExpanded
                )
            },
            selectedMedication = prescriptionDetailState[firstRow].selectedMedication,
            selectedUnits = prescriptionDetailState[firstRow].selectedUnits,
            medicationList = prescriptionDetailState[firstRow].medicationList,
            onSelectMedicationChange = { newMedication ->
                onSelectMedicationChange(
                    timeOfDay,
                    firstRow,
                    newMedication
                )
            },
            onSelectUnitsChange = { newUnit ->
                onSelectUnitsChange(
                    timeOfDay,
                    firstRow,
                    newUnit
                )
            },
            textFieldColors = textFieldColors,
            timeOfDay = timeOfDay,
            rowIndex = firstRow,
            errors = errors
        )

        if (isSecondRowVisible && prescriptionDetailState.size > 1) {
            Spacer(modifier = Modifier.height(8.dp))
            MedicationRowWithError(
                time = time,
                dosage = prescriptionDetailState[secondRow].dosage,
                onDosageChange = { newDosage ->
                    onDosageChange(
                        timeOfDay,
                        secondRow,
                        newDosage
                    )
                },
                isMedicationExpanded = prescriptionDetailState[secondRow].isMedicationExpanded,
                onMedicationExpandedChange = {
                    onMedicationExpandedChange(
                        timeOfDay,
                        secondRow,
                        !prescriptionDetailState[secondRow].isMedicationExpanded
                    )
                },
                isUnitsExpanded = prescriptionDetailState[secondRow].isUnitsExpanded,
                onUnitsExpandedChange = {
                    onUnitsExpandedChange(
                        timeOfDay,
                        secondRow,
                        !prescriptionDetailState[secondRow].isUnitsExpanded
                    )
                },
                selectedMedication = prescriptionDetailState[secondRow].selectedMedication,
                selectedUnits = prescriptionDetailState[secondRow].selectedUnits,
                medicationList = prescriptionDetailState[secondRow].medicationList,
                onSelectMedicationChange = { newMedication ->
                    onSelectMedicationChange(
                        timeOfDay,
                        secondRow,
                        newMedication
                    )
                },
                onSelectUnitsChange = { newUnit ->
                    onSelectUnitsChange(
                        timeOfDay,
                        secondRow,
                        newUnit
                    )
                },
                textFieldColors = textFieldColors,
                timeOfDay = timeOfDay,
                rowIndex = secondRow,
                errors = errors
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    isSecondRowVisible = !isSecondRowVisible
                    onAddPrescriptionRow(timeOfDay)
                },
                modifier = Modifier
                    .height(45.dp)
                    .background(
                        color = appColors.primaryDark,
                        shape = RoundedCornerShape(47.dp)
                    )
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = appColors.primaryDark,
                    contentColor = Color.White,
                    disabledContainerColor = appColors.primaryLight,
                    disabledContentColor = Color.Black
                )
            ) {
                Text(
                    text = if (isSecondRowVisible && prescriptionDetailState.size > 1) "Remove" else "Add",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationRowWithError(
    time: String,
    dosage: Int,
    onDosageChange: (Int) -> Unit,
    isMedicationExpanded: Boolean,
    onMedicationExpandedChange: (Boolean) -> Unit,
    isUnitsExpanded: Boolean,
    onUnitsExpandedChange: (Boolean) -> Unit,
    selectedMedication: Medication,
    selectedUnits: String,
    medicationList: List<Medication>,
    onSelectMedicationChange: (Medication) -> Unit,
    onSelectUnitsChange: (String) -> Unit,
    textFieldColors: TextFieldColors,
    timeOfDay: TimeOfDay,
    rowIndex: Int,
    errors: Map<String, String>
) {
    if (!validateTime(time)) return

    val unitsList = listOf("mg", "l")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top // Align items to the top for better layout
    ) {
        // Medication dropdown with error
        Column(modifier = Modifier.weight(1f)) {
            ExposedDropdownMenuBox(
                expanded = isMedicationExpanded,
                onExpandedChange = { onMedicationExpandedChange(isMedicationExpanded) }
            ) {
                TextField(
                    modifier = Modifier.menuAnchor(),
                    value = selectedMedication.name ?: "",
                    onValueChange = { },
                    readOnly = true,
                    colors = textFieldColors
                )

                ExposedDropdownMenu(
                    expanded = isMedicationExpanded,
                    onDismissRequest = { onMedicationExpandedChange(isMedicationExpanded) },
                    scrollState = rememberScrollState()
                ) {
                    medicationList.forEachIndexed { _, medication ->
                        DropdownMenuItem(
                            text = { Text(text = "${medication.name}") },
                            onClick = {
                                onSelectMedicationChange(medication)
                                onMedicationExpandedChange(isMedicationExpanded)
                            }
                        )
                    }
                }
            }
            if (errors.containsKey("${timeOfDay.name.lowercase()}Medication") && rowIndex == firstRow) {
                Text(
                    text = errors["${timeOfDay.name.lowercase()}Medication"]!!,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            } else if (errors.containsKey("${timeOfDay.name.lowercase()}Medication") && rowIndex == secondRow) {
                Text(
                    text = errors["${timeOfDay.name.lowercase()}Medication"]!!,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp)) // Add some space if no error
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Dosage input with error
        Column(modifier = Modifier.weight(0.7f)) {
            OutlinedTextField(
                value = if (dosage == 0) "" else dosage.toString(),
                onValueChange = { onDosageChange(it.toIntOrNull() ?: 0) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = textFieldColors,
                shape = RoundedCornerShape(5.dp)
            )
            if (errors.containsKey("${timeOfDay.name.lowercase()}Dosage") && rowIndex == firstRow) {
                Text(
                    text = errors["${timeOfDay.name.lowercase()}Dosage"]!!,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            } else if (errors.containsKey("${timeOfDay.name.lowercase()}Dosage") && rowIndex == secondRow) {
                Text(
                    text = errors["${timeOfDay.name.lowercase()}Dosage"]!!,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp)) // Add some space if no error
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Units dropdown with error
        Column(modifier = Modifier.weight(1f)) {
            ExposedDropdownMenuBox(
                expanded = isUnitsExpanded,
                onExpandedChange = { onUnitsExpandedChange(isUnitsExpanded) }
            ) {
                TextField(
                    value = selectedUnits,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.menuAnchor(),
                    colors = textFieldColors
                )
                ExposedDropdownMenu(
                    expanded = isUnitsExpanded,
                    onDismissRequest = { onUnitsExpandedChange(isUnitsExpanded) }
                ) {
                    unitsList.forEach { unit ->
                        DropdownMenuItem(
                            text = { Text(unit) },
                            onClick = {
                                onSelectUnitsChange(unit)
                                onUnitsExpandedChange(isUnitsExpanded)
                            }
                        )
                    }
                }
            }
            if (errors.containsKey("${timeOfDay.name.lowercase()}Units") && rowIndex == firstRow) {
                Text(
                    text = errors["${timeOfDay.name.lowercase()}Units"]!!,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            } else if (errors.containsKey("${timeOfDay.name.lowercase()}Units") && rowIndex == secondRow) {
                Text(
                    text = errors["${timeOfDay.name.lowercase()}Units"]!!,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp)) // Add some space if no error
            }
        }
    }
}


fun validateTime(time: String): Boolean {
    val regex = Regex("^(?:[01]\\d|2[0-3]):[0-5]\\d\$")
    return regex.matches(time)
}

@Preview
@Composable
fun PreviewSchedulePrescriptions() {
     SchedulePrescriptionsScreen(
         prescriptionRepository = PrescriptionRepository(),
         medicationRepository = MedicationRepository(),
         navigateToDailyMedicationTracker = { }
     )
}



