package com.panashecare.assistant.model.objects

data class DailyMedicationLog(
    var id: String? = null,
    val date: String? = null,
    val medicationId: String? = null,
    val wasTaken: Boolean? = null,
    val timeOfDay: String? = null
) {
    fun toJson(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "date" to date,
            "medicationId" to medicationId,
            "wasTaken" to wasTaken,
            "timeOfDay" to timeOfDay
        )
    }
}
