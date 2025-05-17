package com.panashecare.assistant.model.objects

data class DailyMedicationLog(
    var id: String? = null, // date of log is the unique id
    val date: String? = null,
    val morningIntake: List<Intake> = emptyList(),
    val afternoonIntake: List<Intake> = emptyList(),
    val eveningIntake: List<Intake> = emptyList(),
) {
    fun toJson(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "date" to date,
            "morningIntake" to morningIntake.map { item ->
                mapOf(
                    "medicationId" to item.medicationId,
                    "wasTaken" to item.wasTaken
                )
            },
            "afternoonIntake" to afternoonIntake.map { item ->
                mapOf(
                    "medicationId" to item.medicationId,
                    "wasTaken" to item.wasTaken
                )
            },
            "eveningIntake" to eveningIntake.map { item ->
                mapOf(
                    "medicationId" to item.medicationId,
                    "wasTaken" to item.wasTaken
                )
            },
        )
    }

}

data class Intake(
    val medicationId: String? = null,
    val wasTaken: Boolean? = null,
)
