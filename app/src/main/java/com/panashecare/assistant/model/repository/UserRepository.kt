package com.panashecare.assistant.model.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.panashecare.assistant.model.objects.Shift
import com.panashecare.assistant.model.objects.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserRepository(
    private val database: DatabaseReference = Firebase.database.getReference("users")
) {

    private val users = mutableListOf<User>()

    fun getUsers() {
        database.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.getValue<List<User>>()?.let {
                        users.clear()
                        users.addAll(it)
                    }
                }
            }
    }

    fun getCarers(): Flow<List<User>> = callbackFlow {

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { dataSnapshot ->
                    val id = dataSnapshot.child("id").getValue(String::class.java)
                    val firstName = dataSnapshot.child("firstName").getValue(String::class.java)
                    val lastName = dataSnapshot.child("lastName").getValue(String::class.java)
                    val phoneNumber = dataSnapshot.child("phoneNumber").getValue(String::class.java)
                    val email = dataSnapshot.child("email").getValue(String::class.java)
                    val isAdminValue = dataSnapshot.child("isAdmin").getValue(Boolean::class.java)
                    val patientFirstName = dataSnapshot.child("patientFirstName").getValue(String::class.java)
                    val patientLastName = dataSnapshot.child("patientLastName").getValue(String::class.java)

                    User(id, firstName, lastName, phoneNumber, email, isAdminValue, patientFirstName, patientLastName)
                }
                val carers = users.filter { it.isAdmin == false }
                trySend(carers)

             }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

            database.addValueEventListener(listener)
        awaitClose { database.removeEventListener(listener) }
    }

    fun getUserRealtime(userId: Int): Flow<User?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue<List<User>>()?.let {
                    users[userId]
                }
            }

            override fun onCancelled(err: DatabaseError) {
                Log.e("Something went wrong: ", err.message)
                close(err.toException())
            }
        }
        database.addValueEventListener(listener)
        awaitClose { database.removeEventListener(listener) }
    }

    fun getUsersRealtime(): Flow<User?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                users.clear()
                snapshot.getValue<List<User>>()?.let {
                    users.addAll(it)
                }
            }

            override fun onCancelled(err: DatabaseError) {
                Log.e("Something went wrong: ", err.message)
                close(err.toException())
            }
        }
        database.addValueEventListener(listener)
        awaitClose { database.removeEventListener(listener) }
    }

    fun getUserByEmail(email: String, callback: (User?) -> Unit) {
        database.get().addOnSuccessListener { snapshot ->
            val matchedUser = snapshot.children.mapNotNull { it.getValue(User::class.java) }
                .find { it.email == email }
            callback(matchedUser)
        }.addOnFailureListener {
            callback(null)
        }
    }


    fun saveUser(user: User, onComplete: (Boolean) -> Unit) {
        Log.d("Register", "About to save user")
        val userId = database.push().key
        user.id = userId
        database.child(user.id.toString()).setValue(user.toJson())
            .addOnCompleteListener { task ->
                Log.d("Register", "saved user successfully")
                onComplete(task.isSuccessful)
            }
        Log.d("Register", "saved user successfully")
    }

    fun deleteUser(id: Int) {
        database.child("$id")
    }

    fun updateUser(id: Int, field: String, newValue: String) {
        database.child(id.toString()).child(field).setValue(newValue)
    }


}
