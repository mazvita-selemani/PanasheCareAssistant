package com.panashecare.assistant.model.objects

/**
 * created simultaneously as an admin/ home owner
 */
data class Patient(
    var id: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val homeOwnerId: String? = null, //relative/ admin
) {
    fun toJson(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "firstName" to firstName,
            "lastName" to lastName,
            "homeOwnerId" to homeOwnerId,
        )
    }
}