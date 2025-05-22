package com.panashecare.assistant.view.shiftManagement

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panashecare.assistant.AppColors
import com.panashecare.assistant.R
import com.panashecare.assistant.access.UserType
import com.panashecare.assistant.components.SearchBar
import com.panashecare.assistant.components.ShiftCard
import com.panashecare.assistant.model.objects.Shift
import com.panashecare.assistant.model.objects.User
import com.panashecare.assistant.model.repository.ShiftRepository
import com.panashecare.assistant.model.repository.UserRepository
import com.panashecare.assistant.viewModel.shiftManagement.ShiftsOverviewState
import com.panashecare.assistant.viewModel.shiftManagement.ShiftsOverviewViewModel
import com.panashecare.assistant.viewModel.shiftManagement.ShiftsOverviewViewModelFactory

@Composable
fun ShiftsOverviewScreen(shiftRepository: ShiftRepository, userRepository: UserRepository,userId: String,modifier: Modifier, navigateToSingleShiftView: (Shift) -> Unit) {

    val viewModel = viewModel<ShiftsOverviewViewModel>(factory = ShiftsOverviewViewModelFactory(shiftRepository, userRepository, userId))


    ShiftsOverview(
        modifier = modifier,
        state = viewModel.state,
        onUpcomingChange = viewModel::onUpcomingShiftsChange,
        navigateToSingleShiftView = { viewModel.state.selectedShift?.let {
            navigateToSingleShiftView(it)
        } },
        onSelectedShiftFocus = viewModel::onSelectedShiftFocus,
        updateShiftStatus = viewModel::updateShiftStatus
    )
}

@Composable
private fun ShiftsOverview(
    modifier: Modifier = Modifier,
    state: ShiftsOverviewState,
    onUpcomingChange: (Boolean) -> Unit,
    onSelectedShiftFocus: (Shift) -> Unit,
    updateShiftStatus: () -> Unit,
    navigateToSingleShiftView: () -> Unit
) {

    val appColors = AppColors()
    val (selectedButton, setSelectedButton) = remember { mutableStateOf("Upcoming") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SearchBar()

        CustomSpacer(10)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Button(
                modifier = Modifier
                    .width(105.dp)
                    .height(35.dp)
                    .background(
                        if (selectedButton == "Upcoming") appColors.primaryDark else Color.White,
                        shape = RoundedCornerShape(size = 47.dp)
                    )
                    .padding(vertical = 3.dp),
                onClick = {
                    setSelectedButton("Upcoming")
                    onUpcomingChange(true)
                },
                border = BorderStroke(1.dp, appColors.primaryDark),
                colors = ButtonColors(
                    containerColor = if (selectedButton == "Upcoming") appColors.primaryDark else Color.White,
                    contentColor = if (selectedButton == "Upcoming") Color.White else appColors.primaryDark,
                    disabledContainerColor = Color.White,
                    disabledContentColor = Color.White
                )
            ) {
                Text(
                    text = "Upcoming",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight(300),
                        textAlign = TextAlign.Center,
                    )
                )
            }

            // Past Button
            Button(
                modifier = Modifier
                    .width(87.dp)
                    .height(35.dp)
                    .background(
                        if (selectedButton == "Past") appColors.primaryDark else Color.White,
                        shape = RoundedCornerShape(size = 47.dp)
                    )
                    .padding(3.dp),
                onClick = {
                    setSelectedButton("Past")
                    onUpcomingChange(false)

                },
                border = BorderStroke(1.dp, appColors.primaryDark),
                colors = ButtonColors(
                    containerColor = if (selectedButton == "Past") appColors.primaryDark else Color.White,
                    contentColor = if (selectedButton == "Past") Color.White else appColors.primaryDark,
                    disabledContainerColor = Color.White,
                    disabledContentColor = Color.White
                )
            ) {
                Text(
                    text = "Past",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight(300),
                        textAlign = TextAlign.Center,
                    )
                )
            }

            // Sort and Filter section (grouped together)
            Row(
                horizontalArrangement = Arrangement.Center, // Center the sort/filter controls
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Sort Text and Icon
                Row(
                    modifier = Modifier.padding(end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sort",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight(300),
                            textAlign = TextAlign.Center,
                        )
                    )
                    Image(
                        painter = painterResource(R.drawable.system_uicons_sort),
                        contentDescription = null
                    )
                }

                // Filter Text and Icon
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight(300),
                            textAlign = TextAlign.Center,
                        )
                    )
                    Image(painter = painterResource(R.drawable.filter), contentDescription = null)
                }
            }
        }


        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (state.shiftsList.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 12.dp),
                ) {
                    itemsIndexed(
                        items = state.shiftsList,
                        key = { _, shiftSingle -> shiftSingle.id!! }
                    ) { _, shiftSingle ->
                        ShiftCard(
                            shift = shiftSingle,
                            user = state.user ?: User(),
                            navigateToSingleShiftView = {
                                onSelectedShiftFocus(shiftSingle)
                                navigateToSingleShiftView() },
                            isCarerViewingList = state.user?.userType == UserType.CARER,
                            updateShiftStatus = {
                                onSelectedShiftFocus(shiftSingle)
                                updateShiftStatus()
                            }
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

/**
 * custom spacer
 */
@Composable
fun CustomSpacer(height: Int) {
    Spacer(modifier = Modifier.height(height.dp))
}

@Preview
@Composable
fun PreviewShiftsOverview() {
   ShiftsOverviewScreen(
       ShiftRepository(),
       UserRepository(),
       "-OQiHw4_QdryQsuIzCkE",
       modifier = Modifier,
       navigateToSingleShiftView = {}
   )
}