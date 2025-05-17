package com.panashecare.assistant.model.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.panashecare.assistant.model.objects.DailyMedicationLog
import com.panashecare.assistant.model.objects.Intake
import com.panashecare.assistant.model.objects.Prescription
import com.panashecare.assistant.model.objects.User

class DailyMedicationLogRepository(
    private val database: DatabaseReference = Firebase.database.getReference("dailyMedicationLogs")
) {

    fun submitLog(log: DailyMedicationLog, onComplete: (Boolean) -> Unit) {
        database.child(log.id.toString()).setValue(log.toJson())
            .addOnCompleteListener { task ->
                Log.d("Panashe Logs", "created Daily Log successfully")
                onComplete(task.isSuccessful)
            }
    }

    fun getLogByDateId(id: String, callback: (DailyMedicationLog?) -> Unit) {
        database.get().addOnSuccessListener { snapshot ->
            Log.d("Panashe Logs", "getting log")
            Log.d("Panashe Logs", "$snapshot")
            val matchedLog = snapshot.children.mapNotNull { it.getValue(DailyMedicationLog::class.java) }
                .find { it.id == id }
            Log.d("Panashe Logs", "matched log: $matchedLog")
            callback(matchedLog)
        }.addOnFailureListener {
            callback(null)
        }
    }

    fun updateLog(id: String, timeOfIntake: String, intake: List<Intake>) {
        // first check if the an entrance already exists for the date
        database.get().addOnSuccessListener { snapshot ->
        val matchedLog = snapshot.children.mapNotNull { it.getValue(DailyMedicationLog::class.java) }
            .find { it.id == id }
            if ( matchedLog != null) {
                database.child(id).child(timeOfIntake).setValue(intake)
                return@addOnSuccessListener
            }

            val newLog = DailyMedicationLog(
                id = id,
                date = id,
                morningIntake = if (timeOfIntake == "morningIntake") intake else emptyList(),
                afternoonIntake = if (timeOfIntake == "afternoonIntake") intake else emptyList(),
                eveningIntake = if (timeOfIntake == "eveningIntake") intake else emptyList()
            )

            submitLog(newLog) {}
        }
    }
}