package com.panashecare.assistant

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults.containerColor
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.panashecare.assistant.model.repository.DailyMedicationLogRepository
import com.panashecare.assistant.model.repository.MedicationRepository
import com.panashecare.assistant.model.repository.PrescriptionRepository
import com.panashecare.assistant.model.repository.ShiftRepository
import com.panashecare.assistant.model.repository.UserRepository
import com.panashecare.assistant.model.repository.VitalsRepository
import com.panashecare.assistant.ui.theme.PanasheCareAssistantTheme
import com.panashecare.assistant.viewModel.authentication.AuthViewModel

sealed class NavDestination(val title: String, val route: Any, val icon: ImageVector){
    object Home: NavDestination(title = "Home", route = com.panashecare.assistant.Home, icon = Icons.Default.Home)
    object Vitals: NavDestination(title = "Vitals", route = VitalsList, icon = Icons.Filled.Favorite)
    object Meds: NavDestination(title = "Meds", route = DailyMedicationTracker, icon = Icons.Default.Info)
    object Profile: NavDestination(title = "Profile", route = DailyMedicationTracker, icon = Icons.Default.Person)
}

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel: AuthViewModel by viewModels()
        val userRepository by lazy { UserRepository() }
        val shiftRepository by lazy { ShiftRepository() }
        val vitalsRepository by lazy { VitalsRepository() }
        val medicationRepository by lazy { MedicationRepository() }
        val prescriptionRepository by lazy { PrescriptionRepository() }
        val dailyMedicationLogRepository by lazy { DailyMedicationLogRepository()}
        setContent {
            PanasheCareAssistantTheme {

                val navController = rememberNavController()
                val items = listOf(
                    NavDestination.Home,
                    NavDestination.Vitals,
                    NavDestination.Meds,
                    NavDestination.Profile
                )

                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color(0xFF6A1B9A)
                        ) {

                                items.forEachIndexed { _, navDestination ->
                                    NavigationBarItem(
                                        selected = currentDestination == navDestination.route,
                                        onClick = {
                                            if (currentDestination != navDestination.route) {
                                                navController.navigate(navDestination.route) {
                                                    popUpTo(navController.graph.startDestinationId) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        icon = {
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        color = if (currentDestination == navDestination.route) Color(0xFF4A148C) else Color.Transparent, // darker purple highlight
                                                        shape = CircleShape
                                                    )
                                                    .padding(8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = navDestination.icon,
                                                    contentDescription = navDestination.title,
                                                    tint = Color.White
                                                )
                                            }
                                        },
                                        label = {
                                            Text(
                                                text = navDestination.title,
                                                color = Color.White,
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = Color.Transparent, // We’re handling it manually
                                            selectedIconColor = Color.White,
                                            unselectedIconColor = Color.White,
                                            selectedTextColor = Color.White,
                                            unselectedTextColor = Color.White
                                        )
                                    )
                                }
                        }
                    }
                ) {innerPadding ->
                    AppNavigation(
                        modifier = Modifier.padding(innerPadding),
                        authViewModel = authViewModel,
                        userRepository = userRepository,
                        shiftRepository = shiftRepository,
                        vitalsRepository = vitalsRepository,
                        medicationRepository = medicationRepository,
                        prescriptionRepository = prescriptionRepository,
                        dailyMedicationLogRepository = dailyMedicationLogRepository,
                        navController = navController
                    )
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PanasheCareAssistantTheme {
        // Greeting("Android")
    }
}