package com.panashecare.assistant.view.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panashecare.assistant.components.FormField
import com.panashecare.assistant.viewModel.authentication.ForgotPasswordViewModel
import kotlinx.coroutines.delay

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = viewModel(),
    onBackToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val buttonModifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 32.dp)

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        if (uiState.successMessage.isNotEmpty() || uiState.errorMessage.isNotEmpty()) {
            delay(3000)
            viewModel.clearMessages()
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Forgot Password",
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        FormField(
            value = uiState.email,
            onChange = viewModel::onEmailChange,
            label = "Email",
            placeholder = "Enter your email",
            modifier = modifier
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.sendPasswordResetEmail() },
            modifier = buttonModifier,
            colors = ButtonColors(
                containerColor = Color.Black,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.Black
            )
        ) {
            Text(text = "Send Reset Link")
        }

        if (uiState.successMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = uiState.successMessage, color = MaterialTheme.colorScheme.primary)
        }

        if (uiState.errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { onBackToLogin() },
            modifier = buttonModifier,
            colors = ButtonColors(
                containerColor = Color.Black,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.Black
            )
        ) {
            Text(text = "Back to Login")
        }
    }
}
