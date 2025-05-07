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
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging
import com.panashecare.assistant.components.FormField
import com.panashecare.assistant.viewModel.authentication.AuthState
import com.panashecare.assistant.viewModel.authentication.AuthViewModel
import kotlinx.coroutines.tasks.await

// commenting out some sections to test authentication from yt tutorial
@Composable
fun LoginScreen(
    /*email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,*/
    modifier: Modifier,
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onAuthenticated: () -> Unit
) {

    // TODO() add states to view model uistate manager
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {

        val token = Firebase.messaging.token.await()
        Log.d("FCM token:", token)

        when(authState.value){
            is AuthState.Authenticated -> {
                onAuthenticated()
                subscribeToNotificationsTopic()
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
            value = email,
            onChange = { email = it },
            label = "Email",
            placeholder = "Enter your email",
            modifier = modifier
        )

        Spacer(modifier = modifier.height(10.dp))

        FormField(
            value = password,
            onChange = { password = it },
            label = "Password",
            placeholder = "Enter your password",
            modifier = modifier
        )

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = {
                authViewModel.login(email, password)
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

fun subscribeToNotificationsTopic() {
    FirebaseMessaging.getInstance().subscribeToTopic("shifts")
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM", "Subscribed to 'shifts' topic successfully")
            } else {
                Log.e("FCM", "Topic subscription failed", task.exception)
            }
        }
}

/*

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen(){
    PanasheCareAssistantTheme {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        Login(
            onPasswordChange = { password = it },
            onEmailChange = { email = it },
            modifier = Modifier,
            email = email,
            password = password
        )
    }
}
*/


