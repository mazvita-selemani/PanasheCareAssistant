package com.panashecare.assistant.view.medication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panashecare.assistant.components.FormField
import com.panashecare.assistant.components.HeaderSingle
import com.panashecare.assistant.components.SystemButton
import com.panashecare.assistant.model.repository.MedicationRepository
import com.panashecare.assistant.viewModel.medication.CreateMedicationViewModel
import com.panashecare.assistant.viewModel.medication.CreateMedicationViewModelFactory
import com.panashecare.assistant.viewModel.medication.MedicationState

@Composable
fun CreateMedicationScreen(
    modifier: Modifier = Modifier,
    medicationRepository: MedicationRepository,
    navigateToMedicationInventory: () -> Unit,
) {
    val viewModel = viewModel<CreateMedicationViewModel>(factory = CreateMedicationViewModelFactory(medicationRepository))

    CreateMedication(
        modifier = modifier,
        state = viewModel.state,
        viewModel = viewModel,
        navigateToMedicationInventory = { if(viewModel.validateFields()) {
            viewModel.createMedication()
            navigateToMedicationInventory()
        } }
    )


}

@Composable
fun CreateMedication(
    modifier: Modifier = Modifier,
    state : MedicationState,
    viewModel: CreateMedicationViewModel,
    navigateToMedicationInventory: () -> Unit,
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        HeaderSingle("Add New Medication")

        FormField(
            value = state.name,
            onChange = { viewModel.onFieldChange("name", it) },
            modifier = Modifier,
            label = "Medication Name",
            placeholder = "e.g., Paracetamol",
            error = state.errors["name"]
        )

        Spacer(modifier = Modifier.height(8.dp))

        FormField(
            value = state.unit,
            onChange = { viewModel.onFieldChange("unit", it) },
            modifier = Modifier,
            label = "Unit",
            placeholder = "e.g., mg or l",
            error = state.errors["unit"]
        )

        Spacer(modifier = Modifier.height(8.dp))

        FormField(
            value = state.totalInStockRaw,
            onChange = { viewModel.onFieldChange("totalInStock", it) },
            modifier = Modifier,
            label = "Total in Stock",
            placeholder = "e.g., 100",
            error = state.errors["totalInStock"]
        )

        Spacer(modifier = Modifier.height(8.dp))

        FormField(
            value = state.minimumStockAcceptableRaw,
            onChange = { viewModel.onFieldChange("minimumStockAcceptable", it) },
            modifier = Modifier,
            label = "Minimum Acceptable Stock",
            placeholder = "e.g., 10",
            error = state.errors["minimumStockAcceptable"]
        )

        Spacer(modifier = Modifier.height(24.dp))

        SystemButton(buttonText = "Add To Inventory", onNavigationClick = { navigateToMedicationInventory()}, width = 180)
    }
}
