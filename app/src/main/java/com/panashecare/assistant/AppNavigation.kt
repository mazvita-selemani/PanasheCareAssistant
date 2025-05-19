package com.panashecare.assistant

import android.util.Log
import androidx.compose.runtime.Composable
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
import com.panashecare.assistant.view.authentication.LoginScreen
import com.panashecare.assistant.view.authentication.RegisterScreen
import com.panashecare.assistant.view.medication.DailyMedicationTrackerScreen
import com.panashecare.assistant.view.medication.SchedulePrescriptionsScreen
import com.panashecare.assistant.view.shiftManagement.CreateNewShiftScreen
import com.panashecare.assistant.view.shiftManagement.ShiftsOverviewScreen
import com.panashecare.assistant.view.shiftManagement.UpdateShiftScreen
import com.panashecare.assistant.view.shiftManagement.ViewShiftScreen
import com.panashecare.assistant.view.vitals.LogVitalsScreen
import com.panashecare.assistant.view.vitals.ViewVitalsScreen
import com.panashecare.assistant.viewModel.authentication.AuthViewModel
import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object Register

@Serializable
data class Home(val user: String)

@Serializable
data class Profile(val userId: String)

@Serializable
data class SingleShiftView(val shiftId: String, val userId: String)

@Serializable
data class UpdateShift(val shiftId: String, val userId: String)

@Serializable
data class ShiftList(val userId: String)

@Serializable
object CreateNewShift

@Serializable
object VitalsList

@Serializable
data class VitalsLog(val userId: String)

@Serializable
object SchedulePrescriptions

@Serializable
data class DailyMedicationTracker(val userId: String)

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
                modifier = Modifier,
                authViewModel = authViewModel,
                onAuthenticated = { user ->
                    navController.navigate(Home(user = user.id!!))
                },
                onNavigateToRegister = { navController.navigate(Register) },
                repository = userRepository,
                prescriptionRepository = prescriptionRepository
            )
        }

        composable<Register> {
            RegisterScreen(
                authViewModel = authViewModel,
                onAuthenticated = { navController.navigate(Login) },
                repository = userRepository
            )
        }

        composable<Profile> { backStackEntry ->
            val profile: Profile = backStackEntry.toRoute()
            val userId = profile.userId
            ProfileDetailsScreen(
            modifier = modifier,
            authViewModel = authViewModel,
            navigateToLogin = { navController.navigate(Login) },
            userRepository = userRepository,
                userId = userId
        ) }

        composable<Home> { backStackEntry ->
            val home: Home = backStackEntry.toRoute()
            val userId = home.user
            HomeScreen(
                navigateToProfile = { navController.navigate(SchedulePrescriptions) },
                repository = shiftRepository,
                navigateToCreateShift = { navController.navigate(CreateNewShift) },
                navigateToShiftList = { navController.navigate(ShiftList) },
                modifier = modifier,
                userId = userId,
                navigateToSingleViewForPastShift = { shift ->
                    navController.navigate(SingleShiftView(shiftId = shift.id!!, userId = userId))
                },
                navigateToSingleViewForFutureShift = { shift ->
                    Log.d("Navigation", "Shift ID: ${shift.id}")
                    navController.navigate(SingleShiftView(shiftId = shift.id!!, userId = userId))
                },
                userRepository = userRepository
            )
        }

        composable<CreateNewShift> {
            CreateNewShiftScreen(
                repository = userRepository, shiftRepository = shiftRepository,
                navigateToHome = { navController.navigate(Home(user = "placeHolder")) },
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
                userId = userId
            )
        }

        composable<SingleShiftView>{ backStackEntry ->
            val singleShiftView: SingleShiftView = backStackEntry.toRoute()
            val shiftId = singleShiftView.shiftId
            val userId = singleShiftView.userId
            ViewShiftScreen(
                modifier = modifier,
                shiftId = shiftId,
                shiftRepository = shiftRepository,
                navigateToEditShift = { mShiftId ->
                    navController.navigate(UpdateShift(shiftId = mShiftId, userId = userId))

                },
                userRepository = userRepository,
                userId = userId
            )

        }

        composable<UpdateShift>{ backStackEntry ->
            val updateShift: UpdateShift = backStackEntry.toRoute()
            val shiftId = updateShift.shiftId
            val userId = updateShift.userId
            UpdateShiftScreen(
                modifier = modifier,
                shiftId = shiftId,
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
                navigateToVitalsList = { navController.navigate(VitalsList) },
                userRepository = userRepository,
                userId = userId
            )
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

        composable<DailyMedicationTracker> { backStackEntry ->
            val dailyMedicationTracker: DailyMedicationTracker = backStackEntry.toRoute()
            val userId = dailyMedicationTracker.userId
            DailyMedicationTrackerScreen(
                modifier = modifier,
                prescriptionRepository = prescriptionRepository,
                dailyMedicationLogRepository = dailyMedicationLogRepository,
                medicationRepository = medicationRepository,
                navigateToStockManagement = {},
                userRepository = userRepository,
                userId = userId
            )
        }

    }
}