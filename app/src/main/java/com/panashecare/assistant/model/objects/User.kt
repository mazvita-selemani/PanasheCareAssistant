package com.panashecare.assistant.model.objects

data class User(
    var id: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
    val isAdmin: Boolean? = null,
    val patientFirstName: String? = null,
    val patientLastName: String? = null
) {
    fun toJson(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "firstName" to firstName,
            "lastName" to lastName,
            "phoneNumber" to phoneNumber,
            "email" to email,
            "isAdmin" to isAdmin,
            "patientFirstName" to patientFirstName,
            "patientLastName" to patientLastName
        )
    }

    fun getFullName(): String{
        return "${this.firstName} ${this.lastName}"
    }
}