package com.panashecare.assistant

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.panashecare.assistant.model.objects.User
import com.panashecare.assistant.model.repository.DailyMedicationLogRepository
import com.panashecare.assistant.model.repository.MedicationRepository
import com.panashecare.assistant.model.repository.PrescriptionRepository
import com.panashecare.assistant.model.repository.ShiftRepository
import com.panashecare.assistant.model.repository.UserRepository
import com.panashecare.assistant.model.repository.VitalsRepository
import com.panashecare.assistant.view.HomeScreen
import com.panashecare.assistant.view.ProfileDetailsScreen
import com.panashecare.assistant.view.authentication.ForgotPasswordScreen
import com.panashecare.assistant.view.authentication.LoginScreen
import com.panashecare.assistant.view.authentication.RegisterScreen
import com.panashecare.assistant.view.medication.CreateMedicationScreen
import com.panashecare.assistant.view.medication.DailyMedicationTrackerScreen
import com.panashecare.assistant.view.medication.ManageMedicationInventoryScreen
import com.panashecare.assistant.view.medication.SchedulePrescriptionsScreen
import com.panashecare.assistant.view.shiftManagement.CreateNewShiftScreen
import com.panashecare.assistant.view.shiftManagement.ShiftsOverviewScreen
import com.panashecare.assistant.view.shiftManagement.UpdateShiftScreen
import com.panashecare.assistant.view.shiftManagement.ViewShiftScreen
import com.panashecare.assistant.view.vitals.LogVitalsScreen
import com.panashecare.assistant.view.vitals.ViewVitalsScreen
import com.panashecare.assistant.viewModel.UserSessionViewModel
import com.panashecare.assistant.viewModel.authentication.AuthViewModel
import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object Register

@Serializable
object ForgotPassword

@Serializable
data class Home(val user: String? = null)

@Serializable
data class Profile(val userId: String? = null)

@Serializable
data class SingleShiftView(val shiftId: String? = null, val userId: String? = null)

@Serializable
data class UpdateShift(val shiftId: String? = null, val userId: String? = null)

@Serializable
data class ShiftList(val userId: String? = null)

@Serializable
data class CreateNewShift(val userId: String? = null)

@Serializable
data class VitalsList(val userId: String? = null)

@Serializable
data class VitalsLog(val userId: String? = null)

@Serializable
data class SchedulePrescriptions(val userId: String? = null)

@Serializable
data class DailyMedicationTracker(val userId: String? = null)

@Serializable
data class StockManagement(val userId: String? = null)

@Serializable
data class CreateMedication(val userId: String? = null)

@Composable
fun AppNavigation(
    modifier: Modifier,
    authViewModel: AuthViewModel,
    userSessionViewModel: UserSessionViewModel,
    userRepository: UserRepository,
    shiftRepository: ShiftRepository,
    vitalsRepository: VitalsRepository,
    medicationRepository: MedicationRepository,
    prescriptionRepository: PrescriptionRepository,
    dailyMedicationLogRepository: DailyMedicationLogRepository,
    navController: NavHostController
) {

    NavHost(navController = navController, startDestination = Login) {
        composable<Login> {
            LoginScreen(
                modifier = Modifier,
                authViewModel = authViewModel,
                onAuthenticated = {
                    navController.navigate(Home(user = userSessionViewModel.userId))
                },
                onNavigateToRegister = { navController.navigate(Register) },
                repository = userRepository,
                prescriptionRepository = prescriptionRepository,
                userSessionViewModel = userSessionViewModel,
                onNavigateToForgotPassword = { navController.navigate(ForgotPassword) }
            )
        }

        composable<Register> {
            RegisterScreen(
                authViewModel = authViewModel,
                onAuthenticated = { navController.navigate(Login) },
                repository = userRepository
            )
        }

        composable<ForgotPassword> {
            ForgotPasswordScreen(
                onBackToLogin = { navController.navigate(Login) }
            )
        }

        composable<Profile> {
            val userId = userSessionViewModel.userId!!
            ProfileDetailsScreen(
            modifier = modifier,
            authViewModel = authViewModel,
            navigateToLogin = { navController.navigate(Login) },
            userRepository = userRepository,
                userId = userId
        ) }

        composable<Home> { backStackEntry ->
            val home: Home = backStackEntry.toRoute()
            val userId = home.user ?: userSessionViewModel.userId

            if (userId != null) {
                if (userSessionViewModel.userId == null) {
                    userSessionViewModel.setUserId(userId)
                }

                // Proceed with the rest of your logic using userId
                HomeScreen(
                    navigateToProfile = { navController.navigate(Profile(userId = userId)) },
                    repository = shiftRepository,
                    navigateToCreateShift = { navController.navigate(CreateNewShift(userId = userId)) },
                    navigateToShiftList = { navController.navigate(ShiftList(userId = userId)) },
                    modifier = modifier,
                    userId = userId,
                    navigateToSingleViewForPastShift = { shift ->
                        navController.navigate(SingleShiftView(shiftId = shift.id!!, userId = userId))
                    },
                    navigateToSingleViewForFutureShift = { shift ->
                        navController.navigate(SingleShiftView(shiftId = shift.id!!, userId = userId))
                    },
                    userRepository = userRepository
                )
            } else {
                // Show fallback or error (userId missing)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: user ID not found")
                }
            }

        }

        composable<CreateNewShift> { backStackEntry ->
            val createNewShift: CreateNewShift = backStackEntry.toRoute()
            val userId = createNewShift.userId
            CreateNewShiftScreen(
                repository = userRepository, shiftRepository = shiftRepository,
                navigateToHome = { navController.navigate(Home(user = userId)) },
                modifier = modifier
            )
        }

        composable<ShiftList> { backStackEntry ->
            val shiftList: ShiftList = backStackEntry.toRoute()
            val userId = shiftList.userId
            ShiftsOverviewScreen(
                shiftRepository = shiftRepository,
                modifier = modifier,
                navigateToSingleShiftView = { shift ->
                    navController.navigate(SingleShiftView(shiftId = shift.id!!, userId = userId))
                },
                userRepository = userRepository,
                userId = userId ?: userSessionViewModel.userId!!
            )
        }

        composable<SingleShiftView>{ backStackEntry ->
            val singleShiftView: SingleShiftView = backStackEntry.toRoute()
            val shiftId = singleShiftView.shiftId
            val userId = singleShiftView.userId
            ViewShiftScreen(
                modifier = modifier,
                shiftId = shiftId ?: "",
                shiftRepository = shiftRepository,
                navigateToEditShift = { mShiftId ->
                    navController.navigate(UpdateShift(shiftId = mShiftId, userId = userId))

                },
                userRepository = userRepository,
                userId = userId ?: userSessionViewModel.userId!!
            )

        }

        composable<UpdateShift>{ backStackEntry ->
            val updateShift: UpdateShift = backStackEntry.toRoute()
            val shiftId = updateShift.shiftId
            val userId = updateShift.userId
            UpdateShiftScreen(
                modifier = modifier,
                shiftId = shiftId ?: "",
                shiftRepository = shiftRepository,
                userRepository = userRepository,
                navigateToSingleShiftView = { navController.navigate(SingleShiftView(shiftId = shiftId, userId = userId)) }
            )

        }

        composable<VitalsLog> { backStackEntry ->
            val vitalsLog: VitalsLog = backStackEntry.toRoute()
            val userId = vitalsLog.userId
            LogVitalsScreen(
                modifier = modifier,
                vitalsRepository = vitalsRepository,
                navigateToVitalsList = { navController.navigate(VitalsList(userId = userId)) },
                userRepository = userRepository,
                userId = userId!!
            )
        }

        composable<VitalsList> {
            val userId = userSessionViewModel.userId
            if (userId == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                ViewVitalsScreen(
                    modifier = modifier,
                    vitalsRepository = vitalsRepository,
                    navigateToCreateVitalsLog = { navController.navigate(VitalsLog(userId = userId)) })
            }
        }

        composable<SchedulePrescriptions> {
            val userId = userSessionViewModel.userId
            if (userId == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                SchedulePrescriptionsScreen(
                    modifier = modifier,
                    prescriptionRepository = prescriptionRepository,
                    medicationRepository = medicationRepository,
                    navigateToDailyMedicationTracker = { navController.navigate(DailyMedicationTracker(userId = userId)) }
                )
            }
        }

        composable<DailyMedicationTracker> {
            val userId = userSessionViewModel.userId
            if (userId == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                DailyMedicationTrackerScreen(
                    modifier = modifier,
                    userId = userId,
                    medicationRepository = medicationRepository,
                    prescriptionRepository = prescriptionRepository,
                    dailyMedicationLogRepository = dailyMedicationLogRepository,
                    navigateToStockManagement = { navController.navigate(StockManagement(userId = userId)) },
                    userRepository = userRepository,
                    navigateToCreatePrescriptionScheduleScreen = { navController.navigate(SchedulePrescriptions(userId = userId)) }
                )
            }
        }

        composable<StockManagement> { backStackEntry ->
            val stockManagement: StockManagement = backStackEntry.toRoute()
            val userId = stockManagement.userId
            ManageMedicationInventoryScreen(
                modifier = modifier,
                medicationRepository = medicationRepository,
                navigateToDailyMedicationTracker = { navController.navigate(DailyMedicationTracker(userId = userId)) },
                navigateToCreateMedication = { navController.navigate(CreateMedication(userId = userId)) }
            )
        }

        composable<CreateMedication> { backStackEntry ->
            val createMedication: CreateMedication = backStackEntry.toRoute()
            val userId = createMedication.userId
            CreateMedicationScreen(
                modifier = modifier,
                medicationRepository = medicationRepository,
                navigateToMedicationInventory = { navController.navigate(StockManagement(userId = userId)) }
            )
        }


    }
}