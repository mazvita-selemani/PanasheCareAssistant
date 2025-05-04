package com.panashecare.assistant.model.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.panashecare.assistant.model.objects.MedicationWithDosage
import com.panashecare.assistant.model.objects.Prescription
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class PrescriptionRepository(
    private val database: DatabaseReference = Firebase.database.getReference("prescriptions")
) {

    fun getPrescriptionsRealtime(timeOfDay: String): Flow<PrescriptionResult> = callbackFlow {
        trySend(PrescriptionResult.Loading)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val prescriptionList = snapshot.children.mapNotNull { it.getValue(Prescription::class.java) }

                if (timeOfDay == "morning") {
                    val morningPrescriptions = prescriptionList[0].morningMedication
                    morningPrescriptions?.let { PrescriptionResult.Success(it) }
                        ?.let { trySend(it) }
                    return
                }

                if (timeOfDay == "afternoon") {
                    val afternoonPrescriptions = prescriptionList[0].afternoonMedication
                    afternoonPrescriptions?.let { PrescriptionResult.Success(it) }
                        ?.let { trySend(it) }
                    return
                }

                if (timeOfDay == "evening") {
                    val eveningPrescriptions = prescriptionList[0].eveningMedication
                    eveningPrescriptions?.let { PrescriptionResult.Success(it) }
                        ?.let { trySend(it) }
                    return
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(PrescriptionResult.Error("Database error: ${error.message}"))
                close(error.toException())
            }
        }

        database.addValueEventListener(listener)
        awaitClose { database.removeEventListener(listener) }
    }

    fun createPrescription(prescription: Prescription, onComplete: (Boolean) -> Unit) {
        Log.d("Register", "About to create Prescription")
        val prescriptionId = database.push().key
        prescription.apply { id = prescriptionId }
        database.child(prescription.id.toString()).setValue(prescription.toJson())
            .addOnCompleteListener { task ->
                Log.d("Register", "created Prescription successfully")
                onComplete(task.isSuccessful)
            }
        Log.d("Register", "created Prescription successfully")
    }
    
}

sealed class PrescriptionResult {
    data object Loading : PrescriptionResult()
    data class Success(val prescriptionList: List<MedicationWithDosage> = emptyList()) : PrescriptionResult()
    data class Error(val message: String) : PrescriptionResult()
}