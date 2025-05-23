package com.panashecare.assistant.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panashecare.assistant.R
import com.panashecare.assistant.model.objects.Medication
import com.panashecare.assistant.ui.theme.PanasheCareAssistantTheme

@Composable
fun InventoryCountCard(modifier: Modifier = Modifier, medication: Medication) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(color = Color.Transparent)
    ) {
        // Card
        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth()
                .height(120.dp) // Reduce the actual card height
                .background(color = Color(0xFFE7F7FA), shape = RoundedCornerShape(18.dp))
                .padding(horizontal = 13.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(25.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.bxs_first_aid),
                    contentDescription = null
                )
            }

            // Left Column
            Column {
                Text(
                    text = "Medication",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight(400))
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Stock Count",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight(400))
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Right Column
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${medication.name}",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight(500))
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "${medication.totalInStock}",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight(500))
                )
            }
        }

    }
}


@Preview(showSystemUi = true)
@Composable
fun PreviewInventoryCountCard(){
    PanasheCareAssistantTheme {
       // InventoryCountCard()
    }
}