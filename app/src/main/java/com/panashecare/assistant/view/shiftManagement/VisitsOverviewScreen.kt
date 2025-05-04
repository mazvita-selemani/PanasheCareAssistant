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
import com.panashecare.assistant.components.SearchBar
import com.panashecare.assistant.components.ShiftCard
import com.panashecare.assistant.model.repository.ShiftRepository
import com.panashecare.assistant.viewModel.shiftManagement.ShiftsOverviewState
import com.panashecare.assistant.viewModel.shiftManagement.ShiftsOverviewViewModel
import com.panashecare.assistant.viewModel.shiftManagement.ShiftsOverviewViewModelFactory

@Composable
fun ShiftsOverviewScreen(shiftRepository: ShiftRepository) {

    val viewModel = viewModel<ShiftsOverviewViewModel>(factory = ShiftsOverviewViewModelFactory(shiftRepository))


    ShiftsOverview(
        state = viewModel.state,
        onUpcomingChange = viewModel::onUpcomingShiftsChange ,
    )
}

@Composable
private fun ShiftsOverview(
    modifier: Modifier = Modifier,
    state: ShiftsOverviewState,
    onUpcomingChange: (Boolean) -> Unit
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
            modifier = modifier
                .fillMaxWidth()
                .height(55.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {

            // Upcoming Button
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
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
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

/**
 * custom spacer
 */
@Composable
fun CustomSpacer(height: Int) {
    Spacer(modifier = Modifier.height(height.dp))
}

// TODO() delete this
data class shift(
    val id: Int,
    val healthAideName: String,
    val previousshiftsCount: Int?, // Nullable
    val dayOfshift: String,
    val timeOfshift: String,
    val period: String = "Upcoming",
    val isAdmin: Boolean = true
)

val mockshifts = listOf(
    shift(
        id = 1,
        healthAideName = "Sarah Johnson",
        previousshiftsCount = 3,
        dayOfshift = "Monday, April 21",
        timeOfshift = "10:00 AM"
    ),
    shift(
        id = 2,
        healthAideName = "David Smith",
        previousshiftsCount = null,
        dayOfshift = "Tuesday, April 22",
        timeOfshift = "12:30 PM"
    ),
    shift(
        id = 3,
        healthAideName = "Emily Brown",
        previousshiftsCount = 1,
        dayOfshift = "Wednesday, April 23",
        timeOfshift = "09:15 AM"
    ),
    shift(
        id = 4,
        healthAideName = "Michael Green",
        previousshiftsCount = 2,
        dayOfshift = "Thursday, April 24",
        timeOfshift = "3:45 PM"
    ),
    shift(
        id = 5,
        healthAideName = "Olivia Davis",
        previousshiftsCount = 5,
        dayOfshift = "Friday, April 25",
        timeOfshift = "11:00 AM"
    )
)

@Preview
@Composable
fun PreviewshiftsOverview() {
    ShiftsOverviewScreen(ShiftRepository())
}