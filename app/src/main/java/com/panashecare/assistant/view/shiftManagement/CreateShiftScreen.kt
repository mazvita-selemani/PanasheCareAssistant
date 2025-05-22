package com.panashecare.assistant.view.shiftManagement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Text
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panashecare.assistant.AppColors
import com.panashecare.assistant.components.FormField
import com.panashecare.assistant.components.HeaderSingle
import com.panashecare.assistant.components.HelpIconWithDialog
import com.panashecare.assistant.components.ShiftTimePicker
import com.panashecare.assistant.components.SystemButton
import com.panashecare.assistant.model.objects.Shift
import com.panashecare.assistant.model.objects.ShiftStatus
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

    val shift = Shift(
        adminName = User(firstName = "AJ", lastName = "Simpson"), // pass actual names
        healthAideName = state.selectedCarer,
        shiftDate = startDate,
        shiftDuration = duration,
        shiftEndTime = endTime,
        shiftEndDate = endDate,
        shiftStatus = ShiftStatus.REQUESTED,
        shiftTime = startTime
    )

    CreateNewShift(
        modifier = modifier,
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
            if (viewModel.validateFields()) {
                navigateToHome()
            }
        },
        isExpanded = viewModel.state.isExpanded,
        onExpandedChange = viewModel::updateIsExpanded,
        selectedText = state.selectedCarer?.getFullName() ?: "",
        carersList = state.carers ?: emptyList(),
        onSelectTextChange = viewModel::updateSelectedCarer,
        updateChecked = viewModel::updateChecked
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
    updateChecked: (Boolean) -> Unit,
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderSingle("New Shift")

            HelpIconWithDialog(helpMessage = "Create a new shift by setting the time, date, and assigning a carer. " +
                    "When you're finished, tap 'Send Request' and your chosen carer will be notified of a new shift.")
        }

        CustomSpacer(15)

        ShiftTimePicker(
            state = state,
            updateStartDate = updateStartDate,
            updateEndDate = updateEndDate,
            updateStartTime = updateStartTime,
            updateEndTime = updateEndTime,
            showStartDatePicker = showStartDatePicker,
            showStartTimePicker = showStartTimePicker,
            showEndDatePicker = showEndDatePicker,
            showEndTimePicker = showEndTimePicker,
            updateChecked = updateChecked
        )

        CustomSpacer(30)

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

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { onExpandedChange(isExpanded) }
                ) {
                    FormField(
                        value = selectedText,
                        onChange = { },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                        modifier = Modifier.menuAnchor(),
                        error = state.errors["selectedCarer"],
                        label = "",
                        placeholder = ""
                    )

                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { onExpandedChange(isExpanded) },
                        scrollState = rememberScrollState()
                    ) {

                        carersList.forEachIndexed { _, carer ->
                            DropdownMenuItem(
                                text = { Text(text = carer.getFullName()) },
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

        CustomSpacer(30)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            SystemButton(
                buttonText = "Send Request",
                onNavigationClick = {
                    createShift()
                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewCreateNewShift() {
    val userRepository = UserRepository()
    val shiftRepository = ShiftRepository()

    CreateNewShiftScreen(Modifier, userRepository, shiftRepository, {})
}