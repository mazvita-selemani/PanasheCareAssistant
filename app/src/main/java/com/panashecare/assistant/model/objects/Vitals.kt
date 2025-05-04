package com.panashecare.assistant.model.objects

import android.health.connect.datatypes.BloodPressureRecord
import android.health.connect.datatypes.HeartRateRecord
import android.health.connect.datatypes.OxygenSaturationRecord

data class Vitals(
    var id: String? = null,
    val loggerId: String? = null, // logger could be care taker or homeowner
    val adminId: String? = null, //the current system is designed for in home care, so patients in the system are identified through the admin/homeowner
    val oxygenSaturationRecord: String? = null,
    val bloodPressureRecord: String? = null,
    val heartRateRecord: String? = null,
    val dateOfRecording: String? = null
) {

    fun toJson(): Map<String, Any?>{
        return mapOf(
            "id" to id,
            "loggerId" to loggerId,
            "adminId" to adminId,
            "oxygenSaturationRecord" to oxygenSaturationRecord,
            "bloodPressureRecord" to bloodPressureRecord,
            "heartRateRecord" to heartRateRecord,
            "dateOfRecording" to dateOfRecording
        )
    }
}