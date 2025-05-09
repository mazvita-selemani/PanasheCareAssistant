package com.panashecare.assistant.view.shiftManagement

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
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
import com.panashecare.assistant.components.HeaderSingle
import com.panashecare.assistant.components.TimeDateTextField
import com.panashecare.assistant.model.repository.ShiftRepository
import com.panashecare.assistant.viewModel.shiftManagement.SingleShiftState
import com.panashecare.assistant.viewModel.shiftManagement.SingleShiftViewModel
import com.panashecare.assistant.viewModel.shiftManagement.SingleShiftViewModelFactory
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay


@Composable
fun ViewShiftScreen(modifier: Modifier, shiftId: String, shiftRepository: ShiftRepository, navigateToEditShift: (String) -> Unit){

    val viewModel = viewModel<SingleShiftViewModel>(factory = SingleShiftViewModelFactory(shiftRepository))

    LaunchedEffect(Unit){
        viewModel.getShiftById(shiftId)
    }

    ViewShift(
        modifier = modifier,
        state = viewModel.state,
        navigateToEditShift = { navigateToEditShift(shiftId) },
        shiftId = shiftId
    )
}

@Composable
fun ViewShift(
    modifier: Modifier = Modifier,
    userProfilePicture: Painter? = null,
    state: SingleShiftState,
    shiftId: String,
    navigateToEditShift: () -> Unit
) {

    val appColors = AppColors()
    Column(
        modifier = modifier
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
                    modifier = Modifier
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
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(2.dp)
                        )
                    }

                    if (userProfilePicture == null) {
                        Text(
                            text = "Could not load the image",
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.Start) {
                    Text(state.healthAideName, fontSize = 25.sp, fontWeight = FontWeight(400))
                }
            }

            ReadOnlyTimeShiftPicker(state)
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

            Log.d("ShiftCountdown", "ShiftCountdown in composable: ${state.startDate} ${state.startTime}")

            ShiftCountdown(state.startDate, state.startTime, appColors)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Button(
                    onClick = { navigateToEditShift()},
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


@Composable
private fun ReadOnlyTimeShiftPicker(state: SingleShiftState){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(color = Color.White, shape = RoundedCornerShape(size = 18.dp))
            .padding(16.dp)
    ) {
        Text("Start", fontWeight = FontWeight.Bold, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimeDateTextField(
                value = state.startTime,
                modifier = Modifier.weight(0.5f)
            )

            Icon(
                painter = painterResource(id = R.drawable.clock),
                contentDescription = "Pick time",
                modifier = Modifier
                    .size(24.dp)
            )

            TimeDateTextField(
                value = state.startDate,
                modifier = Modifier.weight(0.5f)
            )

            Icon(
                painter = painterResource(id = R.drawable.calendar),
                contentDescription = "Pick date",
                modifier = Modifier
                    .size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("End", fontWeight = FontWeight.Bold, color = Color.Gray)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = false, onCheckedChange = {})
                Text("Same day")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimeDateTextField(
                value = state.endTime,
                modifier = Modifier.weight(0.5f)
            )

            Icon(
                painter = painterResource(id = R.drawable.clock),
                contentDescription = "Pick time",
                modifier = Modifier
                    .size(24.dp)
            )

            TimeDateTextField(
                value = state.endDate,
                modifier = Modifier.weight(0.5f)
            )

            Icon(
                painter = painterResource(id = R.drawable.calendar),
                contentDescription = "Pick date",
                modifier = Modifier
                    .size(24.dp)
            )
        }
    }
}

@Composable
fun ShiftCountdown(startDate: String, startTime: String, appColors: AppColors) {
    Log.d("ShiftCountdown", "ShiftCountdown in parent function: ${startDate} ${startTime}")

    var countdownText by remember { mutableStateOf("") }

    LaunchedEffect(startDate, startTime) {
        while (true) {
            countdownText = getShiftCountdownText(startDate, startTime)
            delay(1000L)
        }
    }

    Text(
        text = countdownText,
        style = TextStyle(
            fontSize = 32.sp,
            fontWeight = FontWeight(600),
            color = if (countdownText == "Shift completed") Color.Gray else appColors.warning,
            textAlign = TextAlign.Center,
        )
    )
}


private fun getShiftCountdownText(startDate: String, startTime: String): String {

    Log.d("ShiftCountdown", "ShiftCountdown in child function: ${startDate} ${startTime}")

    return try {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")
        val shiftDateTime = LocalDateTime.parse("$startDate $startTime", formatter)
        val now = LocalDateTime.now()
        val duration = Duration.between(now, shiftDateTime)

        when {
            duration.isNegative -> "Shift completed"
            duration.toDays() >= 1 -> "${duration.toDays()} day(s) left"
            else -> {
                val hours = duration.toHours()
                val minutes = duration.toMinutes() % 60
                val seconds = duration.seconds % 60
                String.format(Locale("en"), "%02d:%02d:%02d", hours, minutes, seconds)
            }
        }
    } catch (e: Exception) {
      //  Log.e("ShiftCountdown", "Error calculating countdown: ${e.message}")
        "Invalid date"
    }
}


@Preview
@Composable
fun PreviewViewShift() {
   // ViewShift()
}