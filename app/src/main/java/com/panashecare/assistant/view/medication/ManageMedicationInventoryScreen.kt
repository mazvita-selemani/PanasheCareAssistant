package com.panashecare.assistant.view.medication

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panashecare.assistant.AppColors
import com.panashecare.assistant.components.HeaderSingle
import com.panashecare.assistant.components.InventoryCountCard
import com.panashecare.assistant.model.repository.MedicationRepository
import com.panashecare.assistant.model.repository.MedicationResult
import com.panashecare.assistant.viewModel.medication.ManageInventoryState
import com.panashecare.assistant.viewModel.medication.ManageMedicationInventoryViewModel
import com.panashecare.assistant.viewModel.medication.ManageMedicationInventoryViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun ManageMedicationInventoryScreen(medicationRepository: MedicationRepository) {

    val viewModel = viewModel<ManageMedicationInventoryViewModel>(
        factory = ManageMedicationInventoryViewModelFactory(medicationRepository = medicationRepository)
    )

    ManageMedicationInventory(
        state = viewModel.state,
        result = viewModel.medicationList
    )
}

@Composable
private fun ManageMedicationInventory(
    modifier: Modifier = Modifier,
    state: ManageInventoryState,
    result: MutableStateFlow<MedicationResult>
) {

    val appColors = AppColors()

    Column(
        modifier = modifier
            .padding(15.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        HeaderSingle("Manage your stock")

        Text(
            text = "Update your medication inventory here. Click update to\nconfirm your changes.",
            style = TextStyle(
                fontSize = 13.sp,
            )
        )

        Column(
            modifier = modifier
                .padding(15.dp)
                .fillMaxWidth()
                .heightIn(450.dp, 800.dp)
                .scrollable(rememberScrollState(), Orientation.Vertical),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            when (val stateN = result.collectAsState().value) {
                is MedicationResult.Loading -> Column(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Loading...")
                }

                is MedicationResult.Success -> stateN.MedicationList.forEachIndexed { index, medication ->
                    InventoryCountCard(medication = medication)
                }

                is MedicationResult.Error -> Text("Error: ${stateN.message}")
            }

        }



        Row(
            modifier = modifier
                .align(Alignment.End)
                .padding(5.dp)
        ) {
            Button(
                onClick = {
                    //  navigateToStockManagement() // check for changes otherwise remain disabled
                },
                modifier = Modifier
                    .width(100.dp)
                    .height(45.dp),
                shape = RoundedCornerShape(size = 47.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = appColors.primaryDark,
                    contentColor = Color.White
                )
            ) {
                Text("Add", fontSize = 16.sp, fontWeight = FontWeight(400))
            }
        }


        Row(
            modifier = modifier
                .align(Alignment.CenterHorizontally)
                .padding(5.dp)
        ) {
            Button(
                onClick = {
                    //  navigateToStockManagement() // check for changes otherwise remain disabled
                },
                modifier = Modifier
                    .width(190.dp)
                    .height(45.dp),
                shape = RoundedCornerShape(size = 47.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = appColors.primaryDark,
                    contentColor = Color.White
                )
            ) {
                Text("Update", fontSize = 16.sp, fontWeight = FontWeight(400))
            }
        }

    }
}

@Preview
@Composable
fun PreviewManageInventory() {
    ManageMedicationInventoryScreen(MedicationRepository())
}