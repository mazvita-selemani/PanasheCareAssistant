package com.panashecare.assistant.view.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panashecare.assistant.ui.theme.PanasheCareAssistantTheme

@Composable
fun OTPVerification(
    otp: String,
    onOtpChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier

){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Box (
            modifier = modifier
                .background(color = Color.Blue)
                .width(365.dp)
                .height(274.dp)
        ){  }

        Text(
            text = "OTP Verification",
            style = TextStyle(
                fontSize = 20.sp,
                //fontFamily = FontFamily(Font(R.font.mitr)),
                fontWeight = FontWeight(400),
                color = Color(0xFF11B3CF),
                textAlign = TextAlign.Center,
            )
        )

        Text(
            text = "We have you sent you access code via SMS for Mobile Verification",
            style = TextStyle(
                fontSize = 12.sp,
                //fontFamily = FontFamily(Font(R.font.mitr)),
                fontWeight = FontWeight(300),
                color = Color(0xFF000000),
                textAlign = TextAlign.Center,
            )
        )

        OTPInput(otp= otp, onOtpChange = onOtpChange)

        Spacer(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {

            Text(
                text = "Didn’t Received the OTP?",
                style = TextStyle(
                    fontSize = 12.sp,
                    //fontFamily = FontFamily(Font(R.font.mitr)),
                    fontWeight = FontWeight(300),
                    color = Color(0xFF000000),
                    textAlign = TextAlign.Center,
                )
            )

            Text(
                text = "Resend Code",
                style = TextStyle(
                    fontSize = 12.sp,
                   // fontFamily = FontFamily(Font(R.font.mitr)),
                    fontWeight = FontWeight(500),
                    color = Color(0xFFCB003F),
                    textAlign = TextAlign.Center,
                )
            )

            Button(
                onClick = onSubmit,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = ButtonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.Black
                )
            ) {
                Text(text = "Verify")
            }
        }

    }
}

@Composable
fun OTPInput(
    otp: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequesters = List(6) { remember { FocusRequester() } }
    val focusManager = LocalFocusManager.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(horizontal = 32.dp)
    ) {
        for (i in 0 until 6) {
            val char = otp.getOrNull(i)?.toString() ?: "-"
            OutlinedTextField(
                value = char,
                onValueChange = { value ->
                    if (value.length <= 1 && value.all { it.isDigit() }) {
                        val newOtp = otp.toMutableList()
                        if (otp.length > i) {
                            newOtp[i] = value.first()
                        } else {
                            newOtp.add(value.first())
                        }
                        onOtpChange(newOtp.joinToString(""))
                        // Move focus to next box if not last
                        if (i < 5 && value.isNotEmpty()) {
                            focusRequesters[i + 1].requestFocus()
                        }
                    } else if (value.isEmpty()) {
                        // Remove character and move back
                        val newOtp = otp.toMutableList()
                        if (otp.length > i) {
                            newOtp.removeAt(i)
                            onOtpChange(newOtp.joinToString(""))
                        }
                        if (i > 0) {
                            focusRequesters[i - 1].requestFocus()
                        }
                    }
                },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .focusRequester(focusRequesters[i])
                    .clickable { focusRequesters[i].requestFocus() },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Black
                )
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun OTPVerificationPreview() {

    var text by remember { mutableStateOf("") }

    PanasheCareAssistantTheme {
        OTPVerification(
            modifier = Modifier,
            otp = text,
            onOtpChange = { text = it },
            onSubmit = {  },
        )
    }
}