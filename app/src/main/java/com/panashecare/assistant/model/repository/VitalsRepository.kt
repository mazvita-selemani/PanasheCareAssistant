package com.panashecare.assistant.model.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.panashecare.assistant.model.objects.Shift
import com.panashecare.assistant.model.objects.Vitals
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class VitalsRepository(
    private val database: DatabaseReference = Firebase.database.getReference("vitals")

) {
    fun getVitalsRealtime(): Flow<VitalsResult> = callbackFlow {
        trySend(VitalsResult.Loading)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val vitalsList = snapshot.children.mapNotNull { it.getValue(Vitals::class.java) }
                trySend(VitalsResult.Success(vitalsList))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(VitalsResult.Error("Database error: ${error.message}"))
                close(error.toException())
            }
        }

        database.addValueEventListener(listener)
        awaitClose { database.removeEventListener(listener) }
    }

    fun submitVitalsLog(vitals: Vitals, onComplete: (Boolean) -> Unit) {
        Log.d("Register", "About to create shift")
        val vitalsId = database.push().key
        vitals.apply { id = vitalsId }
        database.child(vitals.id.toString()).setValue(vitals.toJson())
            .addOnCompleteListener { task ->
                Log.d("Register", "created shift successfully")
                onComplete(task.isSuccessful)
            }
        Log.d("Register", "created shift successfully")
    }

}

sealed class VitalsResult {
    data object Loading : VitalsResult()
    data class Success(val vitalsList: List<Vitals> = emptyList()) : VitalsResult()
    data class Error(val message: String) : VitalsResult()
}