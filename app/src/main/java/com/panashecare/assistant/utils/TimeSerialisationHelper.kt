package com.panashecare.assistant.utils

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class TimeSerialisationHelper(){

    fun calculateFormattedShiftDuration(
        startDate: String, startTime: String,
        endDate: String, endTime: String
    ): String {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")

        val startDateTime = LocalDateTime.parse("$startDate $startTime", formatter)
        val endDateTime = LocalDateTime.parse("$endDate $endTime", formatter)

        val duration = Duration.between(startDateTime, endDateTime)

        val days = duration.toDays()
        val hours = duration.minusDays(days).toHours()
        val minutes = duration.minusDays(days).minusHours(hours).toMinutes()

        val dayPart = if (days > 0) "${days}d " else ""
        val hourPart = if (hours > 0) "${hours}h " else ""
        val minutePart = if (minutes > 0) "${minutes}min" else ""

        return (dayPart + hourPart + minutePart).trim()
    }

    /**
     * creates a strong representation of a date
     */
    fun convertDateToString(date: Long): String{
        return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(date))
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun timePickerStateToFormattedString(state: TimePickerState): String {
        val time = LocalTime.of(state.hour, state.minute)
        val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
        return time.format(formatter)
    }

}