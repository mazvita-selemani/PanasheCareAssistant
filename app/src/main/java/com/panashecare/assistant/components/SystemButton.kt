package com.panashecare.assistant.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panashecare.assistant.AppColors

@Composable
fun SystemButton(buttonText: String, onNavigationClick: () -> Unit, width: Int = 155, appColors: AppColors = AppColors()) {
    Button(
        onClick = { onNavigationClick()},
        modifier = Modifier
            .width(width.dp)
            .height(45.dp)
            .background(
                color = appColors.primaryDark,
                shape = RoundedCornerShape(size = 47.dp)
            )
            .padding(3.dp),
        colors = ButtonColors(
            containerColor = appColors.primaryDark,
            contentColor = Color.White,
            disabledContainerColor = appColors.primaryDark,
            disabledContentColor = Color.White
        )
    ) {
        Text(
            text = buttonText,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight(400),
            )
        )
    }
}