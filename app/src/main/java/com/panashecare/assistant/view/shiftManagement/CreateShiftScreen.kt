package com.panashecare.assistant.view.shiftManagement

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePickerState
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
import com.panashecare.assistant.components.HeaderButtonPair
import com.panashecare.assistant.components.ShiftTimePicker
import com.panashecare.assistant.model.objects.Shift
import com.panashecare.assistant.model.objects.ShiftPeriod
import com.panashecare.assistant.model.objects.User
import com.panashecare.assistant.model.repository.ShiftRepository
import com.panashecare.assistant.model.repository.UserRepository
import com.panashecare.assistant.utils.TimeSerialisationHelper
import com.panashecare.assistant.viewModel.shiftManagement.CreateShiftViewModel
import com.panashecare.assistant.viewModel.shiftManagement.CreateShiftViewModelFactory
import com.panashecare.assistant.viewModel.shiftManagement.ShiftScheduleState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewShiftScreen(
    modifier: Modifier,
    repository: UserRepository,
    shiftRepository: ShiftRepository,
    navigateToHome: () -> Unit
) {


    val viewModel = viewModel<CreateShiftViewModel>(
        factory = CreateShiftViewModelFactory(
            repository,
            shiftRepository
        )
    )

    val state = viewModel.state
    val helper = TimeSerialisationHelper()

    val startDate = state.startDate?.let(helper::convertDateToString)
    val endDate = state.endDate?.let(helper::convertDateToString)
    val startTime = state.startTime?.let(helper::timePickerStateToFormattedString)
    val endTime = state.endTime?.let(helper::timePickerStateToFormattedString)

    val duration =
        if (startDate != null && startTime != null && endDate != null && endTime != null) {
            helper.calculateFormattedShiftDuration(
                startDate = startDate,
                startTime = startTime,
                endDate = endDate,
                endTime = endTime
            )
        } else null


    // TODO sort out past and future rendering, note instead of static values use a function to render closest shift after today and closest before today instead

    val shift = Shift(
        adminName = User(firstName = "AJ", lastName = "Simpson"), // pass actual names
        healthAideName = state.selectedCarer,
        currentUser = User(firstName = "AJ", lastName = "Simpson"),
        shiftDate = startDate,
        shiftPeriod = ShiftPeriod.FUTURE,
        shiftDuration = duration,
    )

    CreateNewShift(
        modifier =  modifier,
        state = viewModel.state,
        updateStartDate = viewModel::updateStartDate,
        updateEndDate = viewModel::updateEndDate,
        updateStartTime = viewModel::updateStartTime,
        updateEndTime = viewModel::updateEndTime,
        showStartDatePicker = viewModel::showStartDatePicker,
        showStartTimePicker = viewModel::showStartTimePicker,
        showEndDatePicker = viewModel::showEndDatePicker,
        showEndTimePicker = viewModel::showEndTimePicker,
        createShift = {
            viewModel.createShift(shift)
            navigateToHome()
        },
        isExpanded = viewModel.state.isExpanded,
        onExpandedChange = viewModel::updateIsExpanded,
        selectedText = state.selectedCarer?.let { "${it.firstName} ${it.lastName}" } ?: "",
        carersList = state.carers ?: emptyList(),

        onSelectTextChange = viewModel::updateSelectedCarer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewShift(
    modifier: Modifier = Modifier,
    state: ShiftScheduleState,
    createShift: () -> Unit,
    updateStartDate: (Long) -> Unit,
    updateEndDate: (Long) -> Unit,
    updateStartTime: (TimePickerState) -> Unit,
    updateEndTime: (TimePickerState) -> Unit,
    showStartDatePicker: (Boolean) -> Unit,
    showStartTimePicker: (Boolean) -> Unit,
    showEndDatePicker: (Boolean) -> Unit,
    showEndTimePicker: (Boolean) -> Unit,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    selectedText: String,
    carersList: List<User>,
    onSelectTextChange: (User) -> Unit

) {

    val appColors = AppColors()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        HeaderButtonPair("New Shift", "Send Request") { createShift() }

        CustomSpacer(10)

        ShiftTimePicker(
            state = state,
            updateStartDate = updateStartDate,
            updateEndDate = updateEndDate,
            updateStartTime = updateStartTime,
            updateEndTime = updateEndTime,
            showStartDatePicker = showStartDatePicker,
            showStartTimePicker = showStartTimePicker,
            showEndDatePicker = showEndDatePicker,
            showEndTimePicker = showEndTimePicker
        )

        CustomSpacer(10)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(color = appColors.surface, shape = RoundedCornerShape(size = 20.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select Home Aide",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight(500),
                    color = appColors.formTextPrimary,
                )
            )

            CustomSpacer(5)

            // SearchBar()

            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Log.d("It works", "$carersList")

                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { onExpandedChange(isExpanded) }
                ) {
                    TextField(
                        modifier = modifier.menuAnchor(),
                        value = selectedText ?: "",
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
                    )

                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { onExpandedChange(isExpanded) },
                        scrollState = rememberScrollState()
                    ) {

                        carersList.forEachIndexed { _, carer ->
                            DropdownMenuItem(
                                text = { Text(text = "${carer.firstName} ${carer.lastName}") },
                                onClick = {
                                    onSelectTextChange(carer)
                                    onExpandedChange(isExpanded)
                                }
                            )
                        }
                    }
                }

            }

        }

        CustomSpacer(10)

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
    }
}

@Preview
@Composable
fun PreviewCreateNewShift() {
    val userRepository = UserRepository()
    val shiftRepository = ShiftRepository()

    //  CreateNewShiftScreen(userRepository, shiftRepository)
}