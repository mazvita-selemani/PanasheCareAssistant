package com.panashecare.assistant.view.shiftManagement

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panashecare.assistant.AppColors
import com.panashecare.assistant.components.HeaderButtonPair
import com.panashecare.assistant.components.ProfileCircular
import com.panashecare.assistant.components.ShiftTimePicker
import com.panashecare.assistant.model.objects.Shift
import com.panashecare.assistant.model.objects.User
import com.panashecare.assistant.model.repository.ShiftRepository
import com.panashecare.assistant.model.repository.UserRepository
import com.panashecare.assistant.utils.TimeSerialisationHelper
import com.panashecare.assistant.viewModel.shiftManagement.UpdateShiftState
import com.panashecare.assistant.viewModel.shiftManagement.UpdateShiftViewModel
import com.panashecare.assistant.viewModel.shiftManagement.UpdateShiftViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateShiftScreen(modifier: Modifier, shiftId: String, shiftRepository: ShiftRepository, userRepository: UserRepository, navigateToSingleShiftView: () -> Unit) {

    val viewModel = viewModel<UpdateShiftViewModel>(factory = UpdateShiftViewModelFactory(shiftRepository = shiftRepository, repository = userRepository))

    LaunchedEffect(Unit){
        viewModel.getShiftById(shiftId)
    }

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


    // updating the value in this specific id
    val updatedFields = mapOf(
        "healthAideName" to state.selectedCarer,
        "shiftDate" to startDate,
        "shiftDuration" to duration,
        "shiftEndTime" to endTime,
        "shiftEndDate" to endDate,
        "shiftTime" to startTime
    )

    UpdateShift(
        modifier = modifier,
        state = state,
        updateStartDate = viewModel::updateStartDate,
        updateEndDate = viewModel::updateEndDate,
        updateStartTime = viewModel::updateStartTime,
        updateEndTime = viewModel::updateEndTime,
        showStartDatePicker = viewModel::showStartDatePicker,
        showStartTimePicker = viewModel::showStartTimePicker,
        showEndDatePicker = viewModel::showEndDatePicker,
        showEndTimePicker = viewModel::showEndTimePicker,
        navigateToSingleShiftView = {
            navigateToSingleShiftView()
            viewModel.updateShift(shiftId = shiftId, updatedFields = updatedFields)
        },
        showDropDownMenu = state.showDropDownMenu,
        updateShowDropDownMenu = viewModel::updateShowDropDownMenu,
        isDropDownMenuExpanded = state.isDropDownMenuExpanded,
        updateIsDropDownExpanded = viewModel::updateIsExpanded,
        selectedCarer = state.selectedCarer?.getFullName() ?: "",
        carersList = state.carers ?: emptyList(),
        onSelectCarerChange = viewModel::updateSelectedCarer,
        confirmCarerSelection = { viewModel.confirmSelectedCarer() },
        cancelCarerSelection = { viewModel.cancelSelectedCarer(state.originalShift!!) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateShift(
    modifier: Modifier = Modifier,
    navigateToSingleShiftView: () -> Unit,
    state: UpdateShiftState,
    showDropDownMenu: Boolean,
    updateShowDropDownMenu: (Boolean) -> Unit,
    isDropDownMenuExpanded: Boolean,
    updateIsDropDownExpanded: (Boolean) -> Unit,
    updateStartDate: (Long) -> Unit,
    updateEndDate: (Long) -> Unit,
    updateStartTime: (TimePickerState) -> Unit,
    updateEndTime: (TimePickerState) -> Unit,
    showStartDatePicker: (Boolean) -> Unit,
    showStartTimePicker: (Boolean) -> Unit,
    showEndDatePicker: (Boolean) -> Unit,
    showEndTimePicker: (Boolean) -> Unit,
    confirmCarerSelection: () -> Unit,
    cancelCarerSelection: () -> Unit,
    selectedCarer: String,
    carersList: List<User>,
    onSelectCarerChange: (User) -> Unit,
) {
    val appColors = AppColors()
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        HeaderButtonPair("Update Shift", "Confirm", { navigateToSingleShiftView()})

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

        // profile card
        AnimatedVisibility(
            visible = !showDropDownMenu,
            enter = slideInVertically {
                with(density) { -40.dp.roundToPx() }
            } + expandVertically(
                expandFrom = Alignment.Top
            ) + fadeIn(
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(183.dp)
                    .background(
                        color = appColors.surface,
                        shape = RoundedCornerShape(size = 18.dp)
                    )
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.6f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                     ProfileCircular(profilePictureSize = 90, navigateToProfile = {})

                    Spacer(modifier = Modifier.width(20.dp))

                    Column(horizontalAlignment = Alignment.Start) {
                        Text(state.healthAideName, fontSize = 25.sp, fontWeight = FontWeight(400))
                        Text(
                            "Contact: +44 73689456",
                            fontSize = 18.sp,
                            fontWeight = FontWeight(300)
                        )
                    }
                }

                Row(modifier = Modifier.align(Alignment.End)) {
                    Button(
                        onClick = { updateShowDropDownMenu(showDropDownMenu) },
                        modifier = Modifier
                            .width(155.dp)
                            .height(45.dp),
                        shape = RoundedCornerShape(size = 47.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = appColors.primaryDark,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Change", fontSize = 16.sp, fontWeight = FontWeight(400))
                    }
                }
            }
        }

        //search card
        AnimatedVisibility(
            visible = showDropDownMenu,
            enter = slideInVertically {
                with(density) { -40.dp.roundToPx() }
            } + expandVertically(
                expandFrom = Alignment.Top
            ) + fadeIn(
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(
                        color = appColors.surface,
                        shape = RoundedCornerShape(size = 20.dp)
                    )
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
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

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    ExposedDropdownMenuBox(
                        expanded = isDropDownMenuExpanded,
                        onExpandedChange = { updateIsDropDownExpanded(isDropDownMenuExpanded) }
                    ) {
                        TextField(
                            modifier = Modifier.menuAnchor(),
                            value = selectedCarer,
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropDownMenuExpanded) }
                        )

                        ExposedDropdownMenu(
                            expanded = isDropDownMenuExpanded,
                            onDismissRequest = { updateIsDropDownExpanded(isDropDownMenuExpanded) },
                            scrollState = rememberScrollState()
                        ) {

                            carersList.forEachIndexed { _, carer ->
                                DropdownMenuItem(
                                    text = { Text(text = carer.getFullName()) },
                                    onClick = {
                                        onSelectCarerChange(carer)
                                        updateIsDropDownExpanded(isDropDownMenuExpanded)
                                    }
                                )
                            }
                        }
                    }

                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(
                        onClick = {
                            cancelCarerSelection()
                            updateShowDropDownMenu(showDropDownMenu)
                                  },
                        modifier = Modifier
                            .width(150.dp)
                            .height(45.dp),
                        shape = RoundedCornerShape(size = 47.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = appColors.primaryLight,
                            contentColor = appColors.formTextPrimary
                        )
                    ) {
                        Text("Cancel", fontSize = 16.sp, fontWeight = FontWeight(400))
                    }

                    Button(
                        onClick = {
                            confirmCarerSelection()
                            updateShowDropDownMenu(showDropDownMenu) },
                        modifier = Modifier
                            .width(150.dp)
                            .height(45.dp),
                        shape = RoundedCornerShape(size = 47.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = appColors.primaryDark,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Confirm", fontSize = 16.sp, fontWeight = FontWeight(400))
                    }

                }
            }
        }



        CustomSpacer(10)

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
                    .fillMaxHeight()
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
fun PreviewUpdateShift() {
    // UpdateShift()
}