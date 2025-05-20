package com.panashecare.assistant.view.vitals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panashecare.assistant.components.HeaderButtonPair
import com.panashecare.assistant.components.VitalsCard
import com.panashecare.assistant.model.objects.Vitals
import com.panashecare.assistant.model.repository.VitalsRepository
import com.panashecare.assistant.viewModel.vitals.ViewVitalsState
import com.panashecare.assistant.viewModel.vitals.ViewVitalsViewModel
import com.panashecare.assistant.viewModel.vitals.ViewVitalsViewModelFactory

@Composable
fun ViewVitalsScreen(modifier: Modifier = Modifier, vitalsRepository: VitalsRepository, navigateToCreateVitalsLog: () -> Unit){

    val viewModel = viewModel<ViewVitalsViewModel>(factory = ViewVitalsViewModelFactory(vitalsRepository))

    val state= viewModel.state


    ViewVitals(
        modifier = modifier,
        navigateToCreateVitalsLog = navigateToCreateVitalsLog,
        state = state
    )
}

@Composable
fun ViewVitals(modifier: Modifier = Modifier, navigateToCreateVitalsLog: () -> Unit, state: ViewVitalsState){

    Column(
        modifier = modifier
            .fillMaxSize().fillMaxHeight()
            .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        HeaderButtonPair("View vitals", "Add new", { navigateToCreateVitalsLog() })

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (state.vitalsList.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    itemsIndexed(
                        items = state.vitalsList,
                        key = { _, vitals -> vitals.id!! }
                    ) { _, vitals ->
                        VitalsCard(
                            vitals = vitals
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "There are no shifts to display")
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun ViewVitalsPreview() {
    val dummyVitals = listOf(
        Vitals(id = "1", oxygenSaturationRecord = "72", bloodPressureRecord = "120/80", heartRateRecord = "36.5"),
        Vitals(id = "2", oxygenSaturationRecord = "78", bloodPressureRecord = "125/85", heartRateRecord = "37.0"),
        Vitals(id = "3", oxygenSaturationRecord = "78", bloodPressureRecord = "125/85", heartRateRecord = "37.0"),
        Vitals(id = "4", oxygenSaturationRecord = "78", bloodPressureRecord = "125/85", heartRateRecord = "37.0"),
        Vitals(id = "5", oxygenSaturationRecord = "78", bloodPressureRecord = "125/85", heartRateRecord = "37.0"),
        Vitals(id = "6", oxygenSaturationRecord = "78", bloodPressureRecord = "125/85", heartRateRecord = "37.0"),
        Vitals(id = "7", oxygenSaturationRecord = "78", bloodPressureRecord = "125/85", heartRateRecord = "37.0"),
        Vitals(id = "8", oxygenSaturationRecord = "78", bloodPressureRecord = "125/85", heartRateRecord = "37.0"),
        Vitals(id = "9", oxygenSaturationRecord = "78", bloodPressureRecord = "125/85", heartRateRecord = "37.0"),
    )

    val previewState = ViewVitalsState(vitalsList = dummyVitals)

    ViewVitals(
        state = previewState,
        navigateToCreateVitalsLog = {},
        modifier = Modifier
    )
}
