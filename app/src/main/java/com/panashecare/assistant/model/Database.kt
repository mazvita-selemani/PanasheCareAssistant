package com.panashecare.assistant.model

import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class Database {
    private lateinit var database: DatabaseReference

    fun initializeDatabase() {
        database = Firebase.database.reference
    }

}