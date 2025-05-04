package com.panashecare.assistant.viewModel.shiftManagement

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState

/**
 * state manager interface for shift schedule reusable composable
 */
interface ShiftScheduleState {
    val showStartDatePicker: Boolean
    val showStartTimePicker: Boolean
    val showEndDatePicker: Boolean
    val showEndTimePicker: Boolean
    val startDate: Long?
    @OptIn(ExperimentalMaterial3Api::class)
    val startTime: TimePickerState?
    val endDate: Long?
    @OptIn(ExperimentalMaterial3Api::class)
    val endTime: TimePickerState?
}