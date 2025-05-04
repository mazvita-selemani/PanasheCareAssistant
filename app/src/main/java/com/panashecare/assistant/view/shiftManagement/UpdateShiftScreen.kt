package com.panashecare.assistant.view.shiftManagement

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
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
import com.panashecare.assistant.AppColors
import com.panashecare.assistant.components.HeaderButtonPair
import com.panashecare.assistant.components.SearchBar
import com.panashecare.assistant.components.ShiftTimePicker
import com.panashecare.assistant.viewModel.shiftManagement.ShiftScheduleState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateShift(
    modifier: Modifier = Modifier, state: ShiftScheduleState,
    updateStartDate: (Long) -> Unit,
    updateEndDate: (Long) -> Unit,
    updateStartTime: (TimePickerState) -> Unit,
    updateEndTime: (TimePickerState) -> Unit,
    showStartDatePicker: (Boolean) -> Unit,
    showStartTimePicker: (Boolean) -> Unit,
    showEndDatePicker: (Boolean) -> Unit,
    showEndTimePicker: (Boolean) -> Unit,
) {
    val appColors = AppColors()
    val scrollState = rememberScrollState()
    var showSearchBar by remember { mutableStateOf(false) }
    val density = LocalDensity.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        HeaderButtonPair("Update Shift", "Confirm", {})

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
            visible = !showSearchBar,
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
                    // commented out cause experimenting with navigating to profile
                    // ProfileCircular(profilePictureSize = 90)

                    Spacer(modifier = Modifier.width(20.dp))

                    Column(horizontalAlignment = Alignment.Start) {
                        Text("Ash Misra", fontSize = 25.sp, fontWeight = FontWeight(400))
                        Text("Age: 47", fontSize = 18.sp, fontWeight = FontWeight(300))
                        Text(
                            "Contact: +44 73689456",
                            fontSize = 18.sp,
                            fontWeight = FontWeight(300)
                        )
                    }
                }

                Row(modifier = Modifier.align(Alignment.End)) {
                    Button(
                        onClick = { showSearchBar = true },
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
            visible = showSearchBar,
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

                SearchBar()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(
                        onClick = { showSearchBar = false },
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
                        onClick = { showSearchBar = false },
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
fun PreviewUpdateShift() {
    // UpdateShift()
}