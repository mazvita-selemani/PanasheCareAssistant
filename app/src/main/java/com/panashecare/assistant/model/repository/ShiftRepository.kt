package com.panashecare.assistant.model.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.panashecare.assistant.access.UserType
import com.panashecare.assistant.model.objects.Shift
import com.panashecare.assistant.model.objects.ShiftPeriod
import com.panashecare.assistant.model.objects.ShiftStatus
import com.panashecare.assistant.model.objects.User
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


    private fun getShiftsFlow(
        loadFullList: Boolean = false,
        period: ShiftPeriod,
        user: User? = null
    ): Flow<ShiftResult> = callbackFlow {
        trySend(ShiftResult.Loading)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allShifts = snapshot.children.mapNotNull { it.getValue(Shift::class.java) }
                val validShifts = allShifts.filter { it.shiftStatus != ShiftStatus.CANCELLED }
                val filteredShifts = validShifts.filter { shiftPeriodHelper.calculateShiftPeriod(it) == period }
                val finalShifts = if (user?.userType == UserType.CARER && user.id != null) {
                    filteredShifts.filter { it.healthAideName?.id == user.id }
                } else {
                    filteredShifts
                }

                if (loadFullList) {
                    trySend(ShiftResult.Success(shiftList = finalShifts))
                } else {
                    val latestShift = when (period) {
                        ShiftPeriod.PAST -> shiftPeriodHelper.findClosestPastShift(finalShifts)
                        ShiftPeriod.FUTURE -> shiftPeriodHelper.findClosestFutureShift(finalShifts)
                        else -> null // Handle other periods if needed
                    }
                    trySend(ShiftResult.Success(shift = latestShift))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(ShiftResult.Error("Database error: ${error.message}"))
                close(error.toException())
            }
        }

        database.addValueEventListener(listener)
        awaitClose { database.removeEventListener(listener) }
    }

    fun getLatestPastShift(loadFullList: Boolean = false, user: User? = null): Flow<ShiftResult> {
        return getShiftsFlow(loadFullList, ShiftPeriod.PAST, user)
    }

    fun getLatestFutureShift(loadFullList: Boolean = false, user: User? = null): Flow<ShiftResult> {
        return getShiftsFlow(loadFullList, ShiftPeriod.FUTURE, user)
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

    fun updateShiftFields(shiftId: String, updatedFields: Map<String, Any?>, onComplete: (Boolean) -> Unit) {
        database.child(shiftId).updateChildren(updatedFields)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("ShiftUpdate", "Updated shift fields successfully")
                } else {
                    Log.e("ShiftUpdate", "Failed to update shift fields: ${task.exception?.message}")
                }
                onComplete(task.isSuccessful)
            }
    }

    fun updateShiftStatus(shiftId: String, onComplete: (Boolean) -> Unit) {
        database.child(shiftId).child("shiftStatus").get().addOnSuccessListener { snapshot ->
            val currentStatus = snapshot.getValue(ShiftStatus::class.java)
            val newStatus = if (currentStatus == ShiftStatus.REQUESTED) ShiftStatus.CONFIRMED else ShiftStatus.REQUESTED

            database.child(shiftId).child("shiftStatus").setValue(newStatus)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        }.addOnFailureListener {
            onComplete(false)
        }
    }


    fun cancelShift(shiftId: String, onComplete: (Boolean) -> Unit) {
        database.child(shiftId).child("shiftStatus").get().addOnSuccessListener {
            database.child(shiftId).child("shiftStatus").setValue(ShiftStatus.CANCELLED)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        }.addOnFailureListener {
            onComplete(false)

        }
    }


}

sealed class ShiftResult {
    data object Loading : ShiftResult()
    data class Success(val shift: Shift? = null, val shiftList: List<Shift> = emptyList()) : ShiftResult()
    data class Error(val message: String) : ShiftResult()
}
