package com.panashecare.assistant.view

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panashecare.assistant.AppColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun ProfileDetailsScreen(modifier: Modifier) {
    var fullName by remember { mutableStateOf("Glenda Mangoma") }
    var patientName by remember { mutableStateOf("Panashe Mangoma") }
    var email by remember { mutableStateOf("gmangs@gmail.com") }
    var phone by remember { mutableStateOf("+441234567890") }

    val originalValues = listOf("Glenda Mangoma", "Panashe Mangoma", "gmangs@gmail.com", "+441234567890")
    val currentValues = listOf(fullName, patientName, email, phone)

    val hasChanges = originalValues != currentValues

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
                colors =   CardDefaults. cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(0.dp),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    EditableField(label = "Full name", value = fullName, onValueChange = { fullName = it })
                    HorizontalDivider()
                    EditableField(label = "Patient name", value = patientName, onValueChange = { patientName = it })
                    HorizontalDivider()
                    EditableField(label = "Email address", value = email, onValueChange = { email = it })
                    HorizontalDivider()
                    EditableField(label = "Phone number", value = phone, onValueChange = { phone = it })
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { /* Save logic */ },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(50))
                .width(140.dp)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCD4)),
            enabled = hasChanges
        ) {
            Text(text = "Save", color = Color.White)
        }
    }
}

@Composable
fun EditableField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = AppColors().primaryDark
            )
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}



@Preview(showBackground = true)
@Composable
fun ProfileDetailsScreenPreview() {
    //  Dummy onSave function for the preview
   // ProfileDetailsScreen()
}
