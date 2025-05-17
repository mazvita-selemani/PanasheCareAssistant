package com.panashecare.assistant.view

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panashecare.assistant.AppColors
import com.panashecare.assistant.model.repository.UserRepository
import com.panashecare.assistant.viewModel.ProfileDetailsViewModel
import com.panashecare.assistant.viewModel.ProfileState
import com.panashecare.assistant.viewModel.ProfileViewModelFactory
import com.panashecare.assistant.viewModel.authentication.AuthViewModel

@Composable
fun ProfileDetailsScreen(
    modifier: Modifier,
    authViewModel: AuthViewModel,
    userRepository: UserRepository,
    navigateToLogin: () -> Unit,
    userId: String
) {
    val viewModel = viewModel<ProfileDetailsViewModel>(
        factory = ProfileViewModelFactory(
            userRepository, userId
        )
    )
    val state = viewModel.state.collectAsState().value

    ProfileDetails(
        modifier = modifier,
        authViewModel = authViewModel,
        navigateToLogin = navigateToLogin,
        state = state,
        onFullNameChange = viewModel::onFullNameChange,
        onPatientNameChange = viewModel::onPatientNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPhoneChange = viewModel::onPhoneChange,
        saveChanges = { if(viewModel.validateFields()) viewModel.saveChanges(userId) }
    )
}


@Composable
fun ProfileDetails(
    modifier: Modifier,
    authViewModel: AuthViewModel,
    navigateToLogin: () -> Unit,
    state: ProfileState,
    saveChanges: () -> Unit,
    onFullNameChange: (String) -> Unit,
    onPatientNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit
) {
    val appColors = AppColors()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Your details",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(0.dp),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    EditableField(
                        label = "Full name",
                        value = state.fullName,
                        onValueChange = { onFullNameChange(it) },
                        error = state.errors["fullName"])
                    HorizontalDivider()
                    EditableField(
                        label = "Patient name",
                        value = state.patientName,
                        onValueChange = { onPatientNameChange(it) },
                        error = state.errors["patientName"])
                    HorizontalDivider()
                    EditableField(
                        label = "Email address",
                        value = state.email,
                        onValueChange = { onEmailChange(it) },
                        error = state.errors["email"])
                    HorizontalDivider()
                    EditableField(
                        label = "Phone number",
                        value = state.phone,
                        onValueChange = { onPhoneChange(it) },
                        error = state.errors["phone"])
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { saveChanges() },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(50))
                .width(140.dp)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = appColors.primaryLight),
            enabled = state.hasChanges
        ) {
            Text(text = "Save", color = Color.Black)
        }

        Button(
            onClick = {
                authViewModel.signOut()
                navigateToLogin()
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(50))
                .width(140.dp)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = appColors.primaryDark),
        ) {
            Text("Sign Out")
        }
    }
}

@Composable
fun EditableField(label: String, value: String, onValueChange: (String) -> Unit, error: String? = null) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = AppColors().primaryDark
            )
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth(),
            isError = error != null,
        )
        if (error != null) {
            Text(text = error, color = Color.Red, fontSize = 12.sp)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProfileDetailsScreenPreview() {
    ProfileDetailsScreen(
        modifier = Modifier,
        authViewModel = AuthViewModel(),
        navigateToLogin = { },
        userRepository = UserRepository(),
        userId = "-OPfMnJrTOztgSwyXJAT"
    )
}
