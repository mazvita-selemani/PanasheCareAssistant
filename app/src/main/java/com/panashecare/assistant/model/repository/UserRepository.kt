package com.panashecare.assistant.model.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.panashecare.assistant.access.UserType
import com.panashecare.assistant.model.UserProfileImages
import com.panashecare.assistant.model.objects.Gender
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
                val users = snapshot.children.mapNotNull { dataSnapshot -> dataSnapshot.getValue(User::class.java) }
                val carers = users.filter { it.userType == UserType.CARER }
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

    fun getCustomUserIdByEmail(email: String, onResult: (String?) -> Unit) {
        database.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val customId = snapshot.children.first().key
                        onResult(customId)
                    } else {
                        onResult(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(null)
                }
            })
    }


    fun getUserById(id: String, callback: (User?) -> Unit) {
        database.get().addOnSuccessListener { snapshot ->
            val matchedUser = snapshot.children.mapNotNull { it.getValue(User::class.java) }
                .find { it.id == id }
            callback(matchedUser)
        }.addOnFailureListener {
            callback(null)
        }
    }

    fun saveUser(user: User, onComplete: (Boolean) -> Unit) {
        val userId = database.push().key
        val image = UserProfileImages().getRandomImageForGender(user.gender ?: Gender.OTHER)
        val mUser = user.copy(id = userId, profileImageRef = image)

        if (userId == null) {
            Log.e("Register Message", "Failed to generate user ID")
            onComplete(false)
            return
        }

        database.child(userId).setValue(mUser.toJson())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Register Message", "User saved successfully with ID: $userId")
                } else {
                    Log.e("Register Message", "User save task failed: ${task.exception?.message}")
                }
                onComplete(task.isSuccessful)
            }
            .addOnFailureListener { exception ->
                Log.e("Register Message", "Failed to save user: ${exception.message}", exception)
                onComplete(false)
            }
    }


    fun deleteUser(id: String) {
        database.child(id).removeValue()
    }

    fun updateUser(id: String, fields: Map<String, String>) {
        val updates = fields.mapValues { it.value }
        database.child(id).updateChildren(updates)
    }
}
