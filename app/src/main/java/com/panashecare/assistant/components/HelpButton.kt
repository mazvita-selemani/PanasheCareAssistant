package com.panashecare.assistant.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panashecare.assistant.AppColors

@Composable
fun HelpIconWithDialog(
    helpMessage: String,
    modifier: Modifier = Modifier
) {
    val appColors = AppColors()
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(50.dp).clip(CircleShape).background(appColors.primaryLight).padding(5.dp)
            .clickable { showDialog = true },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "?",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFFFF2C7E)
            )

            Text(
                text = "Help",
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
                color = Color(0xFFFF2C7E)
            )
        }

    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = "Help")
            },
            text = {
                Text(text = helpMessage)
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(
                        text = "Dismiss",
                        style = LocalTextStyle.current.copy(
                            textDecoration = TextDecoration.None
                        )
                    )
                }
            }
        )
    }
}

@Preview
@Composable
fun HelpIconWithDialogPreview() {
    HelpIconWithDialog(helpMessage = "This is a help message.")
}

