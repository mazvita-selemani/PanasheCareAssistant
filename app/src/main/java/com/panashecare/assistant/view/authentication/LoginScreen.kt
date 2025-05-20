package com.panashecare.assistant.view.authentication

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging
import com.panashecare.assistant.components.FormField
import com.panashecare.assistant.model.objects.User
import com.panashecare.assistant.model.repository.PrescriptionRepository
import com.panashecare.assistant.model.repository.UserRepository
import com.panashecare.assistant.viewModel.authentication.AuthState
import com.panashecare.assistant.viewModel.authentication.AuthViewModel
import com.panashecare.assistant.viewModel.authentication.LoginViewModel
import com.panashecare.assistant.viewModel.authentication.LoginViewModelFactory
import com.panashecare.assistant.viewModel.authentication.RegisterViewModel
import com.panashecare.assistant.viewModel.authentication.RegisterViewModelFactory
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(
    modifier: Modifier,
    repository: UserRepository,
    prescriptionRepository: PrescriptionRepository,
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onAuthenticated: (User) -> Unit
) {
    val viewModel = viewModel<LoginViewModel>(factory = LoginViewModelFactory(repository, prescriptionRepository))
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {

        when(authState.value){
            is AuthState.Authenticated -> {
                // temporary workaround for user object passing
                if(viewModel.state.email.isNotEmpty()) {
                    viewModel.getUserAfterLogin(viewModel.state.email) { user ->
                        user?.let {
                            onAuthenticated(it)
                            subscribeUserToShiftNotifications(it)
                            subscribeUserToVitalLogNotifications()
                          //  viewModel.scheduleMedicationNotifications("-OPVVDqxQGr8RPH3dxzO", context) //hardcoded prescriptionId
                        } ?: Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    val buttonModifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 32.dp)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Sign In",
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold
        )

        FormField(
            value = viewModel.state.email,
            onChange = viewModel::onEmailChange,
            label = "Email",
            placeholder = "Enter your email",
            modifier = modifier
        )

        Spacer(modifier = modifier.height(10.dp))

        FormField(
            value = viewModel.state.password,
            onChange = viewModel::onPasswordChange,
            label = "Password",
            placeholder = "Enter your password",
            modifier = modifier
        )

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = {
                authViewModel.login(viewModel.state.email, viewModel.state.password)
            },
            modifier = buttonModifier,
            colors = ButtonColors(
                containerColor = Color.Black,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.Black
            )
        ) {
            Text(text = "Sign In")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {},
            modifier = buttonModifier,
            colors = ButtonColors(
                containerColor = Color.Black,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.Black
            )
        ) {
            Text(text = "Sign In with Google")
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = onNavigateToRegister) { Text("Don't have an account? Sign up") }
    }
}

fun subscribeUserToShiftNotifications(user:User) {
    FirebaseMessaging.getInstance().subscribeToTopic("${user.id}")
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM", "Subscribed to 'shifts' topic successfully")
            } else {
                Log.e("FCM", "Topic subscription failed", task.exception)
            }
        }
}

fun subscribeUserToVitalLogNotifications() {
    FirebaseMessaging.getInstance().subscribeToTopic("vitals")
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM", "Subscribed to 'vitals' topic successfully")
            } else {
                Log.e("FCM", "Topic subscription failed", task.exception)
            }
        }
}
