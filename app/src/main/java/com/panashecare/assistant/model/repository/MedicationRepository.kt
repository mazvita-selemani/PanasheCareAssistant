package com.panashecare.assistant.model.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.panashecare.assistant.model.objects.Medication
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MedicationRepository(
    private val database: DatabaseReference = Firebase.database.getReference("medications")
) {

    fun getMedicationsRealtime(): Flow<MedicationResult> = callbackFlow {
        trySend(MedicationResult.Loading)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val medicationList = snapshot.children.mapNotNull { it.getValue(Medication::class.java) }
                trySend(MedicationResult.Success(MedicationList = medicationList))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(MedicationResult.Error("Database error: ${error.message}"))
                close(error.toException())
            }
        }

        database.addValueEventListener(listener)
        awaitClose { database.removeEventListener(listener) }
    }

    fun getAllMedications(): Flow<List<Medication>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val medicationList = snapshot.children.mapNotNull { it.getValue(Medication::class.java) }
                trySend(medicationList)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        database.addValueEventListener(listener)
        awaitClose { database.removeEventListener(listener) }
    }

    fun createMedication(medication: Medication, onComplete: (Boolean) -> Unit) {
        Log.d("Register", "About to create Medication")
        val medicationId = database.push().key
        medication.apply { id = medicationId }
        database.child(medication.id.toString()).setValue(medication.toJson())
            .addOnCompleteListener { task ->
                Log.d("Register", "created Medication successfully")
                onComplete(task.isSuccessful)
            }
        Log.d("Register", "created Medication successfully")
    }

    // todo update and delete functions

    // update inventory for this medication
    fun updateMedication(medicationId: String, newInventoryLevel: Int, onComplete: (Boolean) -> Unit) {
        Log.d("Stock", "MedicationId: $medicationId")
        Log.d("Stock", "Total In stock: $newInventoryLevel")
        database.child(medicationId).child("totalInStock").setValue(newInventoryLevel)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun getMedicationById(medicationId: String): Flow<Medication?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val medicationList = snapshot.children.mapNotNull { it.getValue(Medication::class.java) }
                trySend(medicationList.find { it.id == medicationId })
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        database.addValueEventListener(listener)
        awaitClose { database.removeEventListener(listener) }
    }
}

sealed class MedicationResult {
    data object Loading : MedicationResult()
    data class Success(val MedicationList: List<Medication> = emptyList()) : MedicationResult()
    data class Error(val message: String) : MedicationResult()
}