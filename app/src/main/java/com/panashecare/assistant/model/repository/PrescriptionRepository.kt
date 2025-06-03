package com.panashecare.assistant.model.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.panashecare.assistant.model.objects.Prescription
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class PrescriptionRepository(
    private val database: DatabaseReference = Firebase.database.getReference("prescriptions")
) {

    fun getPrescriptionsRealtime(): Flow<PrescriptionResult> = callbackFlow {
        trySend(PrescriptionResult.Loading)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val prescription = snapshot.children
                        .firstNotNullOfOrNull { it.getValue(Prescription::class.java) }

                    if (prescription != null) {
                        trySend(PrescriptionResult.Success(prescription))
                    } else {
                        trySend(PrescriptionResult.Error("No prescriptions found."))
                    }
                } catch (e: Exception) {
                    trySend(PrescriptionResult.Error("Error parsing prescriptions: ${e.localizedMessage}"))
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

    fun observePrescriptions(prescriptionId: String): Flow<List<String>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val morning = snapshot.child(prescriptionId).child("morningTime").getValue(String::class.java)
                val evening = snapshot.child(prescriptionId).child("eveningTime").getValue(String::class.java)
                val afternoon = snapshot.child(prescriptionId).child("afternoonTime").getValue(String::class.java)

                val times = listOf(morning!!, evening!!, afternoon!!)
                trySend(times)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        database.addValueEventListener(listener)
        awaitClose { database.removeEventListener(listener) }
    }

    fun getFirstPrescriptionId(onResult: (String?) -> Unit) {
        database.orderByKey().limitToFirst(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val firstChild = snapshot.children.firstOrNull()
                    val firstKey = firstChild?.key
                    onResult(firstKey) // Pass the key back via callback
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Error getting first prescription ID: ${error.message}")
                    onResult(null)
                }
            })
    }



    // update inventory for this medication
    fun updatePrescriptionMedication(prescriptionId: String, timeOfMedication: String, index: Int, newInventoryLevel: Int, onComplete: (Boolean) -> Unit) {
        database.child(prescriptionId)
            .child(timeOfMedication)
            .child(index.toString())
            .child("medication")
            .child("totalInStock").setValue(newInventoryLevel)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }
    
}

sealed class PrescriptionResult {
    data object Loading : PrescriptionResult()
    data class Success(val prescription: Prescription) : PrescriptionResult()
    data class Error(val message: String) : PrescriptionResult()
}