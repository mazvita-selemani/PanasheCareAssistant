package com.panashecare.assistant.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.panashecare.assistant.R
import com.panashecare.assistant.viewModel.shiftManagement.ShiftScheduleState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * This reusable composable is used for viewing, creating and editing shift start and end times
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftTimePicker(
    modifier: Modifier = Modifier,
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

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(color = Color(0xFFF4F4F5),shape = RoundedCornerShape(size = 18.dp))
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
                value = state.startTime?.let { "%02d:%02d".format(it.hour, it.minute) } ?: "",
                modifier = Modifier.weight(0.5f)
            )

            Icon(
                painter = painterResource(id = R.drawable.clock),
                contentDescription = "Pick time",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { showStartTimePicker(true) }
            )

            TimeDateTextField(
                value = state.startDate?.let { dateFormatter.format(Date(it)) } ?: "",
                modifier = Modifier.weight(0.5f)
            )

            Icon(
                painter = painterResource(id = R.drawable.calendar),
                contentDescription = "Pick date",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { showStartDatePicker(true) }
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
                value = state.endTime?.let { "%02d:%02d".format(it.hour, it.minute) } ?: "",
                modifier = Modifier.weight(0.5f)
            )

            Icon(
                painter = painterResource(id = R.drawable.clock),
                contentDescription = "Pick time",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { showEndTimePicker( true ) }
            )

            TimeDateTextField(
                value = state.endDate?.let { dateFormatter.format(Date(it)) } ?: "",
                modifier = Modifier.weight(0.5f)
            )

            Icon(
                painter = painterResource(id = R.drawable.calendar),
                contentDescription = "Pick date",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { showEndDatePicker( true ) }
            )
        }
    }

    // Time and Date Pickers
    if (state.showStartTimePicker) {
        Dialog(onDismissRequest = { showStartTimePicker(false) }) {
            InputTime(
                onDismiss = { showStartTimePicker(false) },
                onConfirm = {
                    updateStartTime(it)
                    showStartTimePicker(false)
                }
            )
        }
    }

    if (state.showStartDatePicker) {
        Dialog(onDismissRequest = { showStartDatePicker(false) }) {
            DatePicker(
                onDateSelected = {
                    updateStartDate(it!!)
                    showStartDatePicker(false)
                },
                onDismiss = { showStartDatePicker(false) }
            )
        }
    }

    if (state.showEndTimePicker) {
        Dialog(onDismissRequest = { showEndTimePicker(false) }) {
            InputTime(
                onDismiss = { showEndTimePicker(false) },
                onConfirm = {
                    updateEndTime(it)
                    showEndTimePicker(false)
                }
            )
        }
    }

    if (state.showEndDatePicker) {
        Dialog(onDismissRequest = { showEndDatePicker(false) }) {
            DatePicker(
                onDateSelected = {
                    updateEndDate(it!!)
                    showEndDatePicker(false)
                },
                onDismiss = { showEndDatePicker(false)}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDateTextField(value: String, modifier: Modifier){

    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        // backgroundColor = Color.White,
        focusedBorderColor = Color.Black,
        unfocusedBorderColor = Color.Black,
        // placeholderColor = Color.Gray,
        focusedTextColor = Color.Black
    )

    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = modifier,
        readOnly = true,
        singleLine = true,
        colors = textFieldColors,
        shape = RoundedCornerShape(15.dp)
    )
}

/**
 * this is the time picker
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputTime(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
){
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true
    )

    Column {
        TimeInput(
            state = timePickerState
        )

        Button(onClick = onDismiss) {
            Text("Dismiss picker")
        }

        Button(onClick = { onConfirm(timePickerState) }) {
            Text("Confirm")
        }
    }

}

/**
 * this is the time picker, creates a Long instance of a Date and visually presents it as a String
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePicker(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}


@Preview(showSystemUi = true)
@Composable
fun DatePickerExample() {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { showDatePicker = true }) {
            Text("Pick a Date")
        }

        // Only show if showDatePicker is true
        if (showDatePicker) {
            DatePicker(
                onDateSelected = { millis ->
                    selectedDateMillis = millis
                    showDatePicker = false
                },
                onDismiss = {
                    showDatePicker = false
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedDateMillis != null) {
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = Date(selectedDateMillis!!)
            Text("Selected date: ${formatter.format(date)}")
        } else {
            Text("No date selected.")
        }
    }
}
