package com.panashecare.assistant.model.objects

enum class ShiftPeriod { PAST, FUTURE}
enum class ShiftStatus{ REQUESTED, CONFIRMED, CANCELLED}

data class Shift(
    var id: String? = null,
    val adminName: User? = null,
    val healthAideName: User? = null,
    val shiftDate: String? = null,
    val shiftEndDate: String? = null,
    val shiftTime: String? = null,
    val shiftEndTime: String? = null,
    val shiftStatus: ShiftStatus? = null,
    val shiftDuration: String? = null

) {

    fun toJson(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "adminName" to adminName,
            "healthAideName" to healthAideName,
            "shiftDate" to shiftDate,
            "shiftDuration" to shiftDuration,
            "shiftEndTime" to shiftEndTime,
            "shiftEndDate" to shiftEndDate,
            "shiftStatus" to shiftStatus,
            "shiftTime" to shiftTime,
        )
    }
}