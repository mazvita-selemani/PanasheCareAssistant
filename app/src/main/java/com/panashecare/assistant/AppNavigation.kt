package com.panashecare.assistant

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.panashecare.assistant.model.repository.MedicationRepository
import com.panashecare.assistant.model.repository.PrescriptionRepository
import com.panashecare.assistant.model.repository.ShiftRepository
import com.panashecare.assistant.model.repository.UserRepository
import com.panashecare.assistant.model.repository.VitalsRepository
import com.panashecare.assistant.view.HomeScreen
import com.panashecare.assistant.view.authentication.LoginScreen
import com.panashecare.assistant.view.authentication.RegisterScreen
import com.panashecare.assistant.view.authentication.SignOut
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

@Composable
fun AppNavigation(
    modifier: Modifier,
    authViewModel: AuthViewModel,
    userRepository: UserRepository,
    shiftRepository: ShiftRepository,
    vitalsRepository: VitalsRepository,
    medicationRepository: MedicationRepository,
    prescriptionRepository: PrescriptionRepository
) {

    val navController = rememberNavController()

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

        composable<Home> {
            HomeScreen(
                navigateToProfile = { navController.navigate(SchedulePrescriptions) },
                repository = shiftRepository,
                navigateToCreateShift = { navController.navigate(CreateNewShift) },
                navigateToShiftList = { navController.navigate(ShiftList) }
            )
        }

        composable<CreateNewShift> {
            CreateNewShiftScreen(
                userRepository, shiftRepository,
                navigateToHome = { navController.navigate(Home) }
            )
        }

        composable<ShiftList> {
            ShiftsOverviewScreen(
                shiftRepository = shiftRepository
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
                vitalsRepository,
                navigateToVitalsList = { navController.navigate(VitalsList) })
        }

        composable<VitalsList> {
            ViewVitalsScreen(
                vitalsRepository = vitalsRepository,
                navigateToCreateVitalsLog = { navController.navigate(VitalsLog) })
        }

        composable<SchedulePrescriptions> { SchedulePrescriptionsScreen(
            prescriptionRepository = prescriptionRepository,
            medicationRepository = medicationRepository,
            navigateToDailyMedicationTracker = { }
        ) }

    }
}