package com.panashecare.assistant.model.objects


data class MedicationWithDosage(val medication: Medication? = null, val dosage: Int? = null)

data class Prescription(
    var id: String? = null,
    val patientId: String? = null,
    val morningMedication: List<MedicationWithDosage>? = null, // Store MedicationWithDosage
    val morningTime: String? = null,
    val afternoonMedication: List<MedicationWithDosage>? = null,
    val afternoonTime: String? = null,
    val eveningMedication: List<MedicationWithDosage>? = null,
    val eveningTime: String? = null,
) {
    fun toJson(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "patientId" to patientId,
            "morningMedication" to morningMedication?.map { item ->  //changed from pair to item
                mapOf(
                    "medication" to item.medication, // Access name from Medication object
                    "dosage" to item.dosage  // Access dosage
                )
            },
            "morningTime" to morningTime,
            "afternoonMedication" to afternoonMedication?.map { item ->  //changed from pair to item
                mapOf(
                    "medication" to item.medication,
                    "dosage" to item.dosage
                )
            },
            "afternoonTime" to afternoonTime,
            "eveningMedication" to eveningMedication?.map { item -> //changed from pair to item
                mapOf(
                    "medication" to item.medication,
                    "dosage" to item.dosage
                )
            },
            "eveningTime" to eveningTime,
        )
    }
}