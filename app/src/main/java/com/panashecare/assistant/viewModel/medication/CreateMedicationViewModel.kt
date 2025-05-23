package com.panashecare.assistant.viewModel.medication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.panashecare.assistant.model.objects.Medication
import com.panashecare.assistant.model.repository.MedicationRepository
import kotlinx.coroutines.launch

class CreateMedicationViewModel(private val repository: MedicationRepository) : ViewModel() {

    var state by mutableStateOf(MedicationState())

    fun onFieldChange(field: String, value: String) {
        state = when (field) {
            "name" -> state.copy(name = value)
            "unit" -> state.copy(unit = value)
            "totalInStock" -> state.copy(totalInStockRaw = value)
            "minimumStockAcceptable" -> state.copy(minimumStockAcceptableRaw = value)
            else -> state
        }
    }

    fun validateFields(): Boolean {
        val errors = mutableMapOf<String, String?>()
        var valid = true

        if (state.name.length < 5 || !state.name[0].isUpperCase()) {
            errors["name"] = "Name must start with a capital letter and be at least 5 characters."
            valid = false
        }

        if (state.unit != "mg" && state.unit != "l") {
            errors["unit"] = "Unit must be 'mg' or 'l'."
            valid = false
        }

        val total = state.totalInStockRaw.toIntOrNull()
        if (state.totalInStockRaw.length > 3 || total == null) {
            errors["totalInStock"] = "Enter a valid number (max 3 digits)."
            valid = false
        }

        val min = state.minimumStockAcceptableRaw.toIntOrNull()
        if (state.minimumStockAcceptableRaw.length > 3 || min == null) {
            errors["minimumStockAcceptable"] = "Enter a valid number (max 3 digits)."
            valid = false
        }

        state = state.copy(
            errors = errors,
            totalInStock = total,
            minimumStockAcceptable = min
        )

        return valid
    }

    fun createMedication() {
        if (!validateFields()) return

        val newMedication = Medication(
            name = state.name.trim(),
            unit = state.unit,
            totalInStock = state.totalInStock,
            minimumStockAcceptable = state.minimumStockAcceptable
        )

        viewModelScope.launch {
            repository.createMedication(newMedication){}
        }
    }
}



data class MedicationState(
    val name: String = "",
    val unit: String = "",
    val totalInStockRaw: String = "",
    val minimumStockAcceptableRaw: String = "",
    val totalInStock: Int? = null,
    val minimumStockAcceptable: Int? = null,
    val errors: Map<String, String?> = emptyMap()
)


class CreateMedicationViewModelFactory(
    private val medicationRepository: MedicationRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateMedicationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateMedicationViewModel(
                medicationRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}