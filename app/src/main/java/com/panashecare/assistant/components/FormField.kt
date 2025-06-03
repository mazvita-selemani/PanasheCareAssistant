package com.panashecare.assistant.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panashecare.assistant.AppColors
import com.panashecare.assistant.ui.theme.PanasheCareAssistantTheme

@Composable
fun FormField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier,
    label: String,
    placeholder: String,
    error: String? = null,
    horizontalPadding: Int = 32,
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    password: Boolean = false
) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = horizontalPadding.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = AppColors().formTextPrimary,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp),
            placeholder = { Text(placeholder, color = Color.Gray) },
            label = { Text(label, color = Color.Black) },
            singleLine = true,
            readOnly = readOnly,
            isError = error != null,
            trailingIcon = trailingIcon
        )
        if (error != null) {
            Text(text = error, color = Color.Red, fontSize = 12.sp)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewFormField() {
    PanasheCareAssistantTheme {
        FormField(
            value = "",
            onChange = { },
            modifier = Modifier,
            label = "Password",
            placeholder = "Enter something"
        )
    }
}