package com.panashecare.assistant.view.shiftManagement

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panashecare.assistant.AppColors
import com.panashecare.assistant.components.HeaderSingle
import com.panashecare.assistant.components.ShiftTimePicker
import com.panashecare.assistant.viewModel.shiftManagement.ShiftScheduleState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewShift(
    modifier: Modifier = Modifier,
    userProfilePicture: Painter? = null,
    state: ShiftScheduleState,
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        HeaderSingle(pageHeader = "Shift Details")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .background(appColors.primaryLight, shape = RoundedCornerShape(size = 18.dp))
                .padding(10.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = modifier
                        .fillMaxHeight(0.3f)
                        .fillMaxWidth(0.45f)
                        .padding(20.dp)
                        .border(
                            width = 3.dp,
                            color = Color(0xFFC911CF),
                            shape = RoundedCornerShape(size = 18.dp)
                        )

                ) {
                    if (userProfilePicture != null) {
                        Image(
                            userProfilePicture,
                            contentDescription = null,
                            modifier = modifier
                                .align(Alignment.Center)
                                .padding(2.dp)
                        )
                    }

                    if (userProfilePicture == null) {
                        Text(
                            text = "Could not load the image",
                            modifier = modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.Start) {
                    Text("Ash Misra", fontSize = 25.sp, fontWeight = FontWeight(400))

                    CustomSpacer(10)

                    Text("Age: 47", fontSize = 18.sp, fontWeight = FontWeight(300))
                }
            }

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
        }

        CustomSpacer(30)

        Column(
            modifier = Modifier
                .fillMaxWidth(.8f)
                .align(Alignment.CenterHorizontally),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Text(
                text = "Time until Home Aide’s shift",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight(300),
                )
            )

            Text(
                text = "00 : 09 : 42",
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight(600),
                    color = appColors.warning,
                    textAlign = TextAlign.Center,
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Button(
                    onClick = {},
                    modifier = Modifier
                        .width(135.dp)
                        .height(45.dp),
                    shape = RoundedCornerShape(size = 47.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appColors.primaryDark,
                        contentColor = Color.White
                    )
                ) {
                    Text("Edit", fontSize = 16.sp, fontWeight = FontWeight(400))
                }

                Button(
                    onClick = { },
                    modifier = Modifier
                        .width(135.dp)
                        .height(45.dp)
                        .border(
                            1.dp,
                            color = appColors.primaryDark,
                            shape = RoundedCornerShape(size = 47.dp)
                        ),
                    shape = RoundedCornerShape(size = 47.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = appColors.primaryDark,
                        containerColor = Color.White
                    )
                ) {
                    Text("Cancel", fontSize = 16.sp, fontWeight = FontWeight(400))
                }

            }
        }

    }
}

@Preview
@Composable
fun PreviewViewShift() {
   // ViewShift()
}