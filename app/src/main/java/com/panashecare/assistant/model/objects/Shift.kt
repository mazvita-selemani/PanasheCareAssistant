package com.panashecare.assistant.model.objects

enum class ShiftPeriod { PAST, FUTURE}
enum class ShiftStatus{ PENDING, CONFIRMED, CANCELLED}

data class Shift(
    var id: String? = null,
    val adminName: User? = null,
    val healthAideName: User? = null,
    val currentUser: User? = null,
    val shiftDate: String? = null,
    val shiftPeriod: ShiftPeriod? =  null,
    val shiftDuration: String? = null

) {

    fun toJson(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "adminName" to adminName,
            "healthAideName" to healthAideName,
            "shiftDate" to shiftDate,
            "shiftPeriod" to shiftPeriod,
            "shiftDuration" to shiftDuration,
        )
    }
}