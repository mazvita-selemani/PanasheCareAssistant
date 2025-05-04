package com.panashecare.assistant.view

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailsScreen(
    onSave: (String, LocalDate?, String, String) -> Unit, // Updated to handle LocalDate?
    initialFullName: String = "John Doe",
    initialDateOfBirth: LocalDate? = LocalDate.of(1990, 5, 15), // Example initial date
    initialPhoneNumber: String = "+1 555-123-4567",
    initialEmail: String = "john.doe@example.com"
) {
    // Use rememberSaveable to survive configuration changes (like screen rotations)
    var fullName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initialFullName))
    }

    var dateOfBirthText by rememberSaveable {
        mutableStateOf(
            initialDateOfBirth?.format(
                DateTimeFormatter.ISO_LOCAL_DATE
            ) ?: ""
        )
    } // Use ISO_LOCAL_DATE
    var phoneNumber by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initialPhoneNumber))
    }
    var email by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initialEmail))
    }


    // Use a derived state for the LocalDate to handle parsing and nullability
    val dateOfBirth: LocalDate? by remember {
        derivedStateOf {
            try {
                if (dateOfBirthText.isNotBlank()) {
                    LocalDate.parse(
                        dateOfBirthText,
                        DateTimeFormatter.ISO_LOCAL_DATE
                    ) // Use ISO_LOCAL_DATE
                } else {
                    null
                }
            } catch (e: DateTimeParseException) {
                null // Or, you could set an error state here to display to the user
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile Details", color = Color.White) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF937047)), // Example color
            )
        },
        bottomBar = {
            //  Save Button
            Button(
                onClick = {
                    onSave(fullName.text, dateOfBirth, phoneNumber.text, email.text)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF937047))
            ) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Save",
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Changes", style = TextStyle(color = Color.White, fontSize = 18.sp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Full Name
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = TextFieldValue(it.text) },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.colors()
            )

            // Date of Birth
            OutlinedTextField(
                value = dateOfBirthText,
                onValueChange = { dateOfBirthText = it },
                label = { Text("Date of Birth (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, // Could also use KeyboardType.DateTime
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.colors(),
                placeholder = { Text(text = "YYYY-MM-DD") }
            )

            // Phone Number
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = TextFieldValue(it.text) },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.colors()
            )

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = TextFieldValue(it.text) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                colors = TextFieldDefaults.colors()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileDetailsScreenPreview() {
    //  Dummy onSave function for the preview
    ProfileDetailsScreen(onSave = { fullName, dateOfBirth, phoneNumber, email ->
        println("Save clicked with: $fullName, $dateOfBirth, $phoneNumber, $email")
    })
}
