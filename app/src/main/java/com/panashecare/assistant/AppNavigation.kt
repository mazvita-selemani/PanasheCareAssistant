package com.panashecare.assistant

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.panashecare.assistant.model.repository.DailyMedicationLogRepository
import com.panashecare.assistant.model.repository.MedicationRepository
import com.panashecare.assistant.model.repository.PrescriptionRepository
import com.panashecare.assistant.model.repository.ShiftRepository
import com.panashecare.assistant.model.repository.UserRepository
import com.panashecare.assistant.model.repository.VitalsRepository
import com.panashecare.assistant.view.HomeScreen
import com.panashecare.assistant.view.ProfileDetailsScreen
import com.panashecare.assistant.view.authentication.LoginScreen
import com.panashecare.assistant.view.authentication.RegisterScreen
import com.panashecare.assistant.view.authentication.SignOut
import com.panashecare.assistant.view.medication.DailyMedicationTrackerScreen
import com.panashecare.assistant.view.medication.SchedulePrescriptionsScreen
import com.panashecare.assistant.view.shiftManagement.CreateNewShiftScreen
import com.panashecare.assistant.view.shiftManagement.ShiftsOverviewScreen
import com.panashecare.assistant.view.vitals.LogVitalsScreen
import com.panashecare.assistant.view.vitals.ViewVitalsScreen
import com.panashecare.assistant.viewModel.authentication.AuthViewModel
import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object Register

@Serializable
object Home

@Serializable
object Profile

@Serializable
object ShiftList

@Serializable
object CreateNewShift

@Serializable
object SignOut

@Serializable
object VitalsList

@Serializable
object VitalsLog

@Serializable
object SchedulePrescriptions

@Serializable
object DailyMedicationTracker

@Composable
fun AppNavigation(
    modifier: Modifier,
    authViewModel: AuthViewModel,
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
                modifier = modifier,
                authViewModel = authViewModel,
                onAuthenticated = { navController.navigate(Home) },
                onNavigateToRegister = { navController.navigate(Register) }
            )
        }

        composable<Register> {
            RegisterScreen(
                authViewModel = authViewModel,
                onAuthenticated = { navController.navigate(Login) },
                repository = userRepository
            )
        }

        composable<Profile> { ProfileDetailsScreen(modifier = modifier) }

        composable<Home> {
            HomeScreen(
                navigateToProfile = { navController.navigate(SchedulePrescriptions) },
                repository = shiftRepository,
                navigateToCreateShift = { navController.navigate(CreateNewShift) },
                navigateToShiftList = { navController.navigate(ShiftList) },
                modifier = modifier
            )
        }

        composable<CreateNewShift> {
            CreateNewShiftScreen(
                repository = userRepository, shiftRepository = shiftRepository,
                navigateToHome = { navController.navigate(Home) },
                modifier = modifier
            )
        }

        composable<ShiftList> {
            ShiftsOverviewScreen(
                shiftRepository = shiftRepository,
                modifier = modifier
            )
        }

        composable<SignOut> {
            SignOut(
                authViewModel = authViewModel,
                navigateToLogin = { navController.navigate(Login) }
            )
        }

        composable<VitalsLog> {
            LogVitalsScreen(
                modifier = modifier,
                vitalsRepository = vitalsRepository,
                navigateToVitalsList = { navController.navigate(VitalsList) })
        }

        composable<VitalsList> {
            ViewVitalsScreen(
                modifier = modifier,
                vitalsRepository = vitalsRepository,
                navigateToCreateVitalsLog = { navController.navigate(VitalsLog) })
        }

        composable<SchedulePrescriptions> {
            SchedulePrescriptionsScreen(
                modifier = modifier,
                prescriptionRepository = prescriptionRepository,
                medicationRepository = medicationRepository,
                navigateToDailyMedicationTracker = { navController.navigate(DailyMedicationTracker) }
            )
        }

        composable<DailyMedicationTracker> {
            DailyMedicationTrackerScreen(
                modifier = modifier,
                prescriptionRepository = prescriptionRepository,
                dailyMedicationLogRepository = dailyMedicationLogRepository,
                medicationRepository = medicationRepository,
                navigateToStockManagement = {})
        }

    }
}