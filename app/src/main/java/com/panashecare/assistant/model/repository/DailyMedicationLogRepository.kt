package com.panashecare.assistant.model.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.panashecare.assistant.model.objects.DailyMedicationLog
import com.panashecare.assistant.model.objects.Prescription

class DailyMedicationLogRepository(
    private val database: DatabaseReference = Firebase.database.getReference("dailyMedicationLogs")
) {

    fun submitLog(log: DailyMedicationLog, onComplete: (Boolean) -> Unit) {
        Log.d("Panashe Logs", "About to create Daily Log")
        val logId = database.push().key
        log.apply { id = logId }
        database.child(log.id.toString()).setValue(log.toJson())
            .addOnCompleteListener { task ->
                Log.d("Panashe Logs", "created Daily Log successfully")
                onComplete(task.isSuccessful)
            }
    }
}