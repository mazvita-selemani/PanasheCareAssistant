package com.panashecare.assistant.viewModel.authentication

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.panashecare.assistant.model.objects.User
import com.panashecare.assistant.model.repository.PrescriptionRepository
import com.panashecare.assistant.model.repository.UserRepository
import com.panashecare.assistant.model.service.LocalNotificationWorker
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class LoginViewModel(private val repository: UserRepository, private val prescriptionRepository: PrescriptionRepository): ViewModel() {

    var state by mutableStateOf(LoginUiState())

    fun scheduleMedicationNotifications(prescriptionId: String, context: Context) {
        viewModelScope.launch {
            prescriptionRepository.observePrescriptions(prescriptionId).collect { times ->
                times.forEach { time ->
                    val message = "Good morning, don't forget to give the patient the medication at $time"
                    scheduleNotificationAt(time, context, message)
                }
            }
        }
    }

    private fun scheduleNotificationAt(time: String, context: Context, message: String) {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val localTime = LocalTime.parse(time, formatter)
        val now = LocalDateTime.now()

        val triggerDateTime = now.withHour(localTime.hour).withMinute(localTime.minute).withSecond(0)
            .let {
                if (it.isBefore(now)) it.plusDays(1) else it
            }

        val delay = Duration.between(now, triggerDateTime).toMillis()

        val work = OneTimeWorkRequestBuilder<LocalNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("message" to message))
            .build()

        WorkManager.getInstance(context).enqueue(work)
    }


    fun onEmailChange(newValue: String){
        state = state.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String){
        state = state.copy(password = newValue)
    }

    fun getUserAfterLogin(email: String, onUserFound: (User?) -> Unit) {
        repository.getUserByEmail(email, onUserFound)
    }
}

class LoginViewModelFactory(private val repository: UserRepository, private val prescriptionRepository: PrescriptionRepository): ViewModelProvider.Factory{

    // checking that the viewmodel can be created
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LoginViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository, prescriptionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}

data class LoginUiState (
    val email : String = "",
    val password: String ="",
)