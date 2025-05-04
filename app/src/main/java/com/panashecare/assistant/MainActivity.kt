package com.panashecare.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.panashecare.assistant.model.repository.MedicationRepository
import com.panashecare.assistant.model.repository.PrescriptionRepository
import com.panashecare.assistant.model.repository.ShiftRepository
import com.panashecare.assistant.model.repository.UserRepository
import com.panashecare.assistant.model.repository.VitalsRepository
import com.panashecare.assistant.ui.theme.PanasheCareAssistantTheme
import com.panashecare.assistant.viewModel.authentication.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel: AuthViewModel by viewModels()
        val userRepository by lazy { UserRepository() }
        val shiftRepository by lazy { ShiftRepository() }
        val vitalsRepository by lazy { VitalsRepository() }
        val medicationRepository by lazy { MedicationRepository() }
        val prescriptionRepository by lazy { PrescriptionRepository() }
        setContent {
            PanasheCareAssistantTheme {
                AppNavigation(
                    modifier = Modifier,
                    authViewModel = authViewModel,
                    userRepository = userRepository,
                    shiftRepository = shiftRepository,
                    vitalsRepository = vitalsRepository,
                    medicationRepository = medicationRepository,
                    prescriptionRepository = prescriptionRepository
                )
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