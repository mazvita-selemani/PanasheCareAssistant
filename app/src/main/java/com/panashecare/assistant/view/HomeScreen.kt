package com.panashecare.assistant.view

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.panashecare.assistant.AppColors
import com.panashecare.assistant.R
import com.panashecare.assistant.access.AccessControl
import com.panashecare.assistant.access.Permission
import com.panashecare.assistant.access.UserType
import com.panashecare.assistant.components.ProfileCircular
import com.panashecare.assistant.components.SearchBar
import com.panashecare.assistant.components.ShiftCard
import com.panashecare.assistant.model.objects.Shift
import com.panashecare.assistant.model.objects.User
import com.panashecare.assistant.model.repository.ShiftRepository
import com.panashecare.assistant.model.repository.ShiftResult
import com.panashecare.assistant.model.repository.UserRepository
import com.panashecare.assistant.viewModel.HomeScreenState
import com.panashecare.assistant.viewModel.HomeScreenViewModel
import com.panashecare.assistant.viewModel.HomeScreenViewModelFactory


@Composable
fun HomeScreen(
    modifier: Modifier,
    userId: String,
    repository: ShiftRepository,
    userRepository: UserRepository,
    navigateToProfile: () -> Unit,
    navigateToCreateShift: () -> Unit,
    navigateToShiftList: () -> Unit,
    navigateToSingleViewForPastShift: (Shift) -> Unit,
    navigateToSingleViewForFutureShift: (Shift) -> Unit
){
    // checking sdk version to see if permission dialog can be used
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        RequestNotificationPermissionDialog()
    }

    val viewModel = viewModel<HomeScreenViewModel>(factory = HomeScreenViewModelFactory(repository, userRepository, userId))
    val futureShift = viewModel.state.futureShift
    val pastShift by lazy { viewModel.state.pastShift }

    Log.d("HomeScreen state in parent composable", "User: ${viewModel.state.user}")

    Home(
        modifier = modifier,
        navigateToProfile = navigateToProfile,
        user = viewModel.state.user ?: User(firstName = "Loading..."),
        navigateToCreateShift = { navigateToCreateShift() },
        navigateToShiftList = { navigateToShiftList() },
        state = viewModel.state,
        viewModel = viewModel,
        navigateToSingleViewForPastShift = { _ ->
            pastShift?.let { navigateToSingleViewForPastShift(it) }
        },

        navigateToSingleViewForFutureShift = { _ ->
            futureShift?.let { navigateToSingleViewForFutureShift(it) }
        }

    )
}


@Composable
fun Home(
    modifier: Modifier = Modifier,
    user: User,
    navigateToProfile: () -> Unit,
    navigateToCreateShift: () -> Unit,
    navigateToShiftList: () -> Unit,
    navigateToSingleViewForPastShift : (Shift) -> Unit,
    navigateToSingleViewForFutureShift : (Shift) -> Unit,
    state: HomeScreenState,
    viewModel: HomeScreenViewModel
) {

    val scrollState = rememberScrollState()
    val appColors = AppColors()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(color = appColors.primarySuperLight, shape = RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ProfileCircular(
                navigateToProfile = { navigateToProfile() }
            )

            // Text in the center
            Text(
                text = if(AccessControl.isAuthorized(user, Permission.ViewAllShifts)) {
                    if (user.userType == UserType.ADMIN) "${user.patientFirstName}’s Upcoming Visits" else "${user.firstName}’s Upcoming shifts"
                } else {
                    "Upcoming shifts"
                },
                style = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.nunito_variablefont_wght)),
                    fontWeight = FontWeight(400),
                    color = Color.Black,
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            )

            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                modifier = Modifier.size(32.dp),
                tint = Color.Black
            )
        }


        HomePageSpacer(20)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AccessControl.WithPermission(
                user = user,
                permission = Permission.ViewAllShifts,
                onAuthorized = {
                    // view all shifts card
                    HomePageNavCards(
                        modifier = Modifier.weight(1f),
                        painterResourceId = R.drawable.icon_park_outline_list,
                        message = "View All Upcoming shifts",
                        onClickNavigation = { navigateToShiftList() },
                        buttonBackgroundColor = appColors.primarySuperLightAlternative,
                        buttonContainerColor = appColors.primarySuperLightAlternative,
                        buttonContentColor = appColors.primaryDarkAlternative,
                        cardBorderColor = appColors.primaryDarkAlternative
                    )
                }
            )

            AccessControl.WithPermission(
                user = user,
                permission = Permission.CreateShifts,
                onAuthorized = {
                    // create new shift card
                    HomePageNavCards(
                        modifier = Modifier.weight(1f),
                        painterResourceId = R.drawable.notes_svgrepo_com,
                        message = "Create a new shift",
                        onClickNavigation = { navigateToCreateShift() },
                        buttonBackgroundColor = appColors.primarySuperLight,
                        buttonContainerColor = appColors.primarySuperLight,
                        buttonContentColor = appColors.primaryDark,
                        cardBorderColor = appColors.primaryDark
                    )
                }
            )
        }

        HomePageSpacer(20)

        Row(
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text(
                text = "Next shift",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight(400),
                    color = appColors.primaryDark,
                    textAlign = TextAlign.Start,
                )
            )}

        HomePageSpacer(10)

        when (val stateN = viewModel.latestFutureState.collectAsState().value) {
            is ShiftResult.Loading -> NoShiftsMessage("Loading...")
            is ShiftResult.Success -> stateN.shift?.let {
                ShiftCard(
                    modifier = Modifier,
                    shift = it,
                    userProfilePicture = painterResource(it.healthAideName?.profileImageRef ?: R.drawable.person_profile),
                    navigateToSingleShiftView = { navigateToSingleViewForFutureShift(it)  }
                )
            } ?: NoShiftsMessage("No future shifts found.")
            is ShiftResult.Error -> Text("Error: ${stateN.message}")
        }

        HomePageSpacer(20)

        Row(
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text(
                text = "Previously Completed",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight(400),
                    color = appColors.primaryDark,
                    textAlign = TextAlign.Start,
                )
            )
        }

        HomePageSpacer(10)

        when (val stateN = viewModel.latestShiftState.collectAsState().value) {
            is ShiftResult.Loading -> NoShiftsMessage("Loading...")
            is ShiftResult.Success -> stateN.shift?.let {
                ShiftCard(
                    modifier = Modifier,
                    shift = it,
                    userProfilePicture = painterResource(it.healthAideName?.profileImageRef ?: R.drawable.person_profile),
                    navigateToSingleShiftView = { navigateToSingleViewForPastShift(it)  }
                )
            } ?: NoShiftsMessage()
            is ShiftResult.Error -> Text("Error: ${stateN.message}")
        }
    }
}

/**
 * custom spacer
 */
@Composable
private fun HomePageSpacer(height: Int) {
    Spacer(modifier = Modifier.height(height.dp))
}

@Composable
private fun NoShiftsMessage(text: String = "No past shifts found."){
    Box(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .height(150.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ){
        Text(text)
    }
}

@Composable
private fun HomePageNavCards(
    modifier: Modifier,
    painterResourceId: Int,
    onClickNavigation: () -> Unit,
    message: String,
    buttonBackgroundColor: Color,
    buttonContainerColor: Color,
    buttonContentColor: Color,
    cardBorderColor: Color
) {
    Box(
        modifier = modifier
            .height(115.dp)
    ) {
        Box(
            modifier = modifier
                .shadow(
                    elevation = 4.dp,
                    spotColor = Color(0x40000000),
                    ambientColor = Color(0x40000000)
                )
                .border(
                    width = 0.5.dp,
                    color = cardBorderColor,
                    shape = RoundedCornerShape(size = 18.dp)
                )
                .fillMaxWidth()
                .height(105.dp)
                .background(
                    color = Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(size = 18.dp)
                )
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(0.75f),
                    text = message,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight(500),
                    )
                )

                Image(
                    modifier = modifier
                        .width(30.dp)
                        .height(30.dp),
                    painter = painterResource(painterResourceId),
                    contentDescription = null,
                )
            }

        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 10.dp)
        ) {

            Button(
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        spotColor = Color(0x40000000),
                        ambientColor = Color(0x40000000)
                    )
                    .width(90.dp)
                    .height(40.dp)
                    .background(
                        color = buttonBackgroundColor,
                        shape = RoundedCornerShape(size = 12.dp)
                    )
                    .padding(3.dp),
                colors = ButtonColors(
                    containerColor = buttonContainerColor,
                    contentColor = buttonContentColor,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color.Transparent
                ),
                onClick = { onClickNavigation() }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "View",
                        style = TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight(400),
                        )
                    )

                    Icon(
                        modifier = modifier
                            .width(18.dp)
                            .height(18.dp),
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class, ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermissionDialog() {
    val permissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

    // Permission is requested only when the composable is first entered
    SideEffect {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    if (!permissionState.status.isGranted) {
        if (permissionState.status.shouldShowRationale) {
            RationaleDialog(permissionState = permissionState)
        } else {
            PermissionDialog(permissionState = permissionState)
        }
    } else {
        Text(text = "Permission Granted!  You can now receive notifications.", color = Color.Transparent)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RationaleDialog(permissionState: PermissionState) {
    AlertDialog(
        onDismissRequest = { /* Don't dismiss */ },
        title = { Text(text = "Notification Permission Required") },
        text = { Text(text = "Notifications are important for this app to function correctly. Please allow them.") },
        confirmButton = {
            Button(onClick = { permissionState.launchPermissionRequest() }) {
                Text(text = "Allow")
            }
        },
        dismissButton = null
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionDialog(permissionState: PermissionState) { // Take PermissionState
    AlertDialog(
        onDismissRequest = { /* Don't dismiss */ },
        title = { Text(text = "Notification Permission") },
        text = { Text(text = "This app needs notification permissions to send you important updates.") },
        confirmButton = {
            Button(onClick = { permissionState.launchPermissionRequest() }) { // Use passed state
                Text(text = "OK")
            }
        },
        dismissButton = null
    )
}

@Preview(showSystemUi = true)
@Composable
fun PreviewHomePage() {
     HomeScreen(
         modifier = Modifier,
         userId = "-OPpeK9nAgqrw3rLpcH5",
         repository = ShiftRepository(),
         userRepository = UserRepository(),
         navigateToProfile = {  },
         navigateToCreateShift = {  },
         navigateToShiftList = {  },
         navigateToSingleViewForPastShift = {  },
         navigateToSingleViewForFutureShift = {  }
     )
}