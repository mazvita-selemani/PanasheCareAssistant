package com.panashecare.assistant.view.authentication

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panashecare.assistant.access.UserType
import com.panashecare.assistant.components.FormField
import com.panashecare.assistant.model.objects.User
import com.panashecare.assistant.model.repository.UserRepository
import com.panashecare.assistant.viewModel.authentication.AuthState
import com.panashecare.assistant.viewModel.authentication.AuthViewModel
import com.panashecare.assistant.viewModel.authentication.RegisterUiState
import com.panashecare.assistant.viewModel.authentication.RegisterViewModel
import com.panashecare.assistant.viewModel.authentication.RegisterViewModelFactory


@Composable
fun RegisterScreen(
    repository: UserRepository,
    onAuthenticated: () -> Unit,
    authViewModel: AuthViewModel
) {
    val viewModel = viewModel<RegisterViewModel>(factory = RegisterViewModelFactory(repository))

    val user = User(
        firstName = viewModel.state.firstName,
        lastName = viewModel.state.lastName,
        email = viewModel.state.email,
        phoneNumber = viewModel.state.phoneNumber,
        userType = if (viewModel.state.isAdmin) UserType.ADMIN else UserType.CARER,
        patientFirstName = viewModel.state.patientFirstName,
        patientLastName = viewModel.state.patientLastName
    )

    Register(
        state = viewModel.state,
        onFirstNameChange = viewModel::onFirstNameChange,
        onLastNameChange = viewModel::onLastNameChange,
        onEmailChange = viewModel::onEmailChange,
        modifier = Modifier,
        authViewModel = authViewModel,
        onAuthenticated = onAuthenticated,
        onPhoneNumberChange = viewModel::onPhoneNumberChange,
        onPatientFirstNameChange = viewModel::onPatientFirstNameChange,
        onPatientLastNameChange = viewModel::onPatientLastNameChange,
        onAdminChecked = viewModel::onAdminCheckedChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        saveUser = { viewModel.registerNewUser(user) }
    )
}

@Composable
fun Register(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    onAuthenticated: () -> Unit,
    state: RegisterUiState,
    saveUser: () -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onPatientFirstNameChange: (String) -> Unit,
    onPatientLastNameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onAdminChecked: (Boolean) -> Unit,
) {

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> onAuthenticated()
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT
            ).show()

            else -> Unit
        }
    }

    val buttonModifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 32.dp)
    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Register",
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold
        )

        FormField(
            value = state.firstName,
            onChange = { onFirstNameChange(it) },
            label = "First Name",
            placeholder = "Enter your first name",
            modifier = modifier,
            error = state.errors["firstName"]
        )

        Spacer(modifier = Modifier.height(10.dp))

        FormField(
            value = state.lastName,
            onChange = { onLastNameChange(it) },
            label = "Last Name",
            placeholder = "Enter your last name",
            modifier = modifier,
            error = state.errors["lastName"]
        )

        Spacer(modifier = Modifier.height(10.dp))

        FormField(
            value = state.email,
            onChange = { onEmailChange(it) },
            label = "Email Address",
            placeholder = "Enter your email",
            modifier = modifier,
            error = state.errors["email"]

        )

        Spacer(modifier = Modifier.height(10.dp))

        FormField(
            value = state.phoneNumber,
            onChange = { onPhoneNumberChange(it) },
            label = "Phone number",
            placeholder = "Enter your phone number",
            modifier = modifier,
            error = state.errors["phoneNumber"]
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = modifier
                .align(Alignment.End)
                .padding(end = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Register as Admin?",
                fontSize = 20.sp
            )

            Spacer(modifier = modifier.width(5.dp))

            Switch(
                checked = state.isAdmin,
                onCheckedChange = { onAdminChecked(it) },
                modifier = modifier.scale(0.75f)
            )
        }

        if (state.isAdmin) {

            Spacer(modifier = Modifier.height(10.dp))

            FormField(
                value = state.patientFirstName,
                onChange = { onPatientFirstNameChange(it) },
                label = "Patient last name",
                placeholder = "Patient last name",
                modifier = modifier,
                error = state.errors["patientFirstName"]
            )

            Spacer(modifier = Modifier.height(10.dp))

            FormField(
                value = state.patientLastName,
                onChange = { onPatientLastNameChange(it) },
                label = "Patient last name",
                placeholder = "Patient last name",
                modifier = modifier,
                error = state.errors["patientLastName"]
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        FormField(
            value = state.password,
            onChange = { onPasswordChange(it) },
            label = "Password",
            placeholder = "Enter your password",
            modifier = modifier,
            error = state.errors["password"]
        )

        Spacer(modifier = Modifier.height(10.dp))

        FormField(
            value = state.confirmPassword,
            onChange = { onConfirmPasswordChange(it) },
            label = "Confirm Password",
            placeholder = "Confirm your password",
            modifier = modifier,
            error = state.errors["confirmPassword"]
        )

        Spacer(modifier = Modifier.height(10.dp))



        Button(
            onClick = {
                saveUser.invoke()
                authViewModel.signUp(state.email, state.password)
            },
            modifier = buttonModifier,
            colors = ButtonColors(
                containerColor = Color.Black,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.Black
            )
        ) {
            Text(text = "Register")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // TODO
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

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegister() {
    RegisterScreen(UserRepository(), {}, AuthViewModel())
}

