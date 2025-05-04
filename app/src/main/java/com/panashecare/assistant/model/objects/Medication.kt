package com.panashecare.assistant.model.objects

data class Medication(
    var id: String? = null,
    val name: String? = null,
    val unit: String? = null,
    val totalInStock: Int? = null,
    val minimumStockAcceptable: Int? = null
) {

    fun toJson(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "unit" to unit,
            "totalInStock" to totalInStock,
            "minimumStockAcceptable" to minimumStockAcceptable
        )
    }
}