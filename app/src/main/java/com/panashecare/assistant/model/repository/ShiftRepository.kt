package com.panashecare.assistant.model.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.panashecare.assistant.model.objects.Shift
import com.panashecare.assistant.model.objects.ShiftPeriod
import com.panashecare.assistant.utils.ShiftPeriodHelper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ShiftRepository(
    private val database: DatabaseReference = Firebase.database.getReference("shifts")

) {
    private val shiftPeriodHelper = ShiftPeriodHelper()

    fun getShiftById(shiftId: String, onResult: (Shift?) -> Unit) {
        database.child(shiftId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val shift = snapshot.getValue(Shift::class.java)
                onResult(shift)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ShiftRepository", "Error fetching shift: ${error.message}")
                onResult(null)
            }
        })
    }

    fun getShiftsRealtime(): Flow<ShiftResult> = callbackFlow {
        trySend(ShiftResult.Loading)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val shiftList = snapshot.children.mapNotNull { it.getValue(Shift::class.java) }
                trySend(ShiftResult.Success(shiftList = shiftList))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(ShiftResult.Error("Database error: ${error.message}"))
                close(error.toException())
            }
        }

        database.addValueEventListener(listener)
        awaitClose { database.removeEventListener(listener) }
    }


    fun getLatestPastShift(loadFullList: Boolean = false): Flow<ShiftResult> = callbackFlow {
        trySend(ShiftResult.Loading) // Start with loading state

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val shiftList = snapshot.children.mapNotNull { it.getValue(Shift::class.java) }
                val pastShifts = shiftList.filter { shiftPeriodHelper.calculateShiftPeriod(it) == ShiftPeriod.PAST }

                // load all past shifts
                if(loadFullList){
                    trySend(ShiftResult.Success(shiftList = pastShifts))
                    return
                }

                // get latest past shift
                val latestShift = shiftPeriodHelper.findClosestPastShift(pastShifts)
                trySend(ShiftResult.Success(latestShift))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(ShiftResult.Error("Database error: ${error.message}"))
                close(error.toException()) // Close the flow on failure
            }
        }

        database.addValueEventListener(listener)
        awaitClose { database.removeEventListener(listener) }
    }


    fun getLatestFutureShift(loadFullList: Boolean = false): Flow<ShiftResult> = callbackFlow {
        trySend(ShiftResult.Loading) // Start with loading state

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val shiftList = snapshot.children.mapNotNull { it.getValue(Shift::class.java) }
                val futureShifts = shiftList.filter { shiftPeriodHelper.calculateShiftPeriod(it) == ShiftPeriod.FUTURE }

                // load all future shifts
                if(loadFullList){
                    trySend(ShiftResult.Success(shiftList = futureShifts))
                    return
                }

                // get latest future shift

                val latestShift = shiftPeriodHelper.findClosestFutureShift(futureShifts)

                trySend(ShiftResult.Success(latestShift))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(ShiftResult.Error("Database error: ${error.message}"))
                close(error.toException()) // Close the flow on failure
            }
        }

        database.addValueEventListener(listener)
        awaitClose { database.removeEventListener(listener) }
    }

    fun createShift(shift: Shift, onComplete: (Boolean) -> Unit) {
        Log.d("Register", "About to create shift")
        val shiftId = database.push().key
        shift.apply { id = shiftId }
        database.child(shift.id.toString()).setValue(shift.toJson())
            .addOnCompleteListener { task ->
                Log.d("Register", "created shift successfully")
                onComplete(task.isSuccessful)
            }
        Log.d("Register", "created shift successfully")
    }

}

sealed class ShiftResult {
    data object Loading : ShiftResult()
    data class Success(val shift: Shift? = null, val shiftList: List<Shift> = emptyList()) : ShiftResult()
    data class Error(val message: String) : ShiftResult()
}
