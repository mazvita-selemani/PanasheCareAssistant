package com.panashecare.assistant.utils

import android.util.Log
import com.google.firebase.Timestamp
import com.panashecare.assistant.model.objects.Shift
import com.panashecare.assistant.model.objects.ShiftPeriod
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class ShiftPeriodHelper {

    fun calculateShiftPeriod(shift: Shift): ShiftPeriod {

        val today = Timestamp.now().toDate()

        val shiftStartDate = Date(shift.shiftDate)

        val period = today.before(shiftStartDate)  // BUG: if date is today it will return false and shift will be put to past, because time is not specific

        if (period){
            return ShiftPeriod.FUTURE
        }

        return ShiftPeriod.PAST
    }

    fun findClosestFutureShift(shifts: List<Shift>): Shift? {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        val today = LocalDate.now()

        return shifts
            .filterNot { it.shiftDate.isNullOrBlank() }
            .mapNotNull { shift ->
                try {
                    val date = LocalDate.parse(shift.shiftDate, formatter)
                    if (date.isAfter(today) || date.isEqual(today)) shift to date else null
                } catch (e: Exception) {
                    Log.e("Closest Shift", "${e.message}")
                    null
                }
            }
            .minByOrNull { it.second }
            ?.first
    }

    fun findClosestPastShift(shifts: List<Shift>): Shift? {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        val today = LocalDate.now()

        return shifts
            .filterNot { it.shiftDate.isNullOrBlank() }
            .mapNotNull { shift ->
                try {
                    val date = LocalDate.parse(shift.shiftDate, formatter)
                    if (date.isBefore(today)) shift to date else null  // BUG: date is before today does not work when shift is on same day
                } catch (e: Exception) {
                    null
                }
            }
            .maxByOrNull { it.second }
            ?.first
    }


}