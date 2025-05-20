package com.panashecare.assistant.model.objects

import com.panashecare.assistant.access.UserType

enum class Gender {FEMALE, MALE, OTHER}

data class User(
    val id: String? = null,
    val gender: Gender? = null,
    val profileImageRef: Int? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
    val userType: UserType? = null,
    val patientFirstName: String? = null,
    val patientLastName: String? = null
) {
    fun toJson(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "gender" to gender,
            "profileImageRef" to profileImageRef,
            "firstName" to firstName,
            "lastName" to lastName,
            "phoneNumber" to phoneNumber,
            "email" to email,
            "userType" to userType?.name,
            "patientFirstName" to patientFirstName,
            "patientLastName" to patientLastName
        )
    }

    fun getFullName(): String{
        return "${this.firstName} ${this.lastName}"
    }
}