package com.panashecare.assistant.model.objects


data class Prescription(
    var id: String? = null,
    val patientId: String? = null,
    val morningMedication: List<Medication>? = null,
    val morningTime: String? = null,
    val afternoonMedication: List<Medication>? = null,
    val afternoonTime: String? = null,
    val eveningMedication: List<Medication>? = null,
    val eveningTime: String? = null,
) {
    fun toJson(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "patientId" to patientId,
            "morningMedication" to morningMedication,
            "morningTime" to morningTime,
            "afternoonMedication" to afternoonMedication,
            "afternoonTime" to afternoonTime,
            "eveningMedication" to eveningMedication,
            "eveningTime" to eveningTime,
        )
    }
}