package com.panashecare.assistant.view.vitals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panashecare.assistant.components.HeaderButtonPair
import com.panashecare.assistant.components.VitalsCard
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
            .fillMaxSize()
            .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        HeaderButtonPair("View vitals", "Add new") { navigateToCreateVitalsLog() }

        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (state.vitalsList.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 12.dp),
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
                // Use weight to center the text properly
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