package com.panashecare.assistant.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panashecare.assistant.AppColors
import com.panashecare.assistant.R
import com.panashecare.assistant.model.objects.Vitals
import com.panashecare.assistant.ui.theme.PanasheCareAssistantTheme
import com.panashecare.assistant.view.shiftManagement.CustomSpacer

@Composable
fun VitalsCard(
    modifier: Modifier = Modifier,
    vitals: Vitals
    ) {

    val verticalPadding = 10.dp
    val appColors = AppColors()

    Column(modifier = modifier
        .padding(verticalPadding)
        .fillMaxWidth()) {

        Row(modifier = modifier.align(Alignment.Start)) {
            Text(
                text = "${vitals.dateOfRecording}",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight(400),
                    textAlign = TextAlign.Center,
                )
            )
        }

        CustomSpacer(10)

        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(71.dp)
                .background(
                    color = appColors.primarySuperLight,
                    shape = RoundedCornerShape(size = 18.dp)
                )
                .padding(start = 13.dp, top = 15.dp, end = 13.dp, bottom = 15.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.fluent_color_heart_20),
                contentDescription = null
            )

            Text(
                text = "${vitals.heartRateRecord}",
                style = TextStyle(
                    fontSize = 20.sp,
                    //  fontFamily = FontFamily(Font(R)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFFF21F61),
                    textAlign = TextAlign.Center,
                )
            )

            Image(painter = painterResource(R.drawable.oxygen_sat), contentDescription = null)

            Text(
                text = "${vitals.oxygenSaturationRecord}",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFF21F61),
                    textAlign = TextAlign.Center,
                )
            )

            Image(painter = painterResource(R.drawable.blood_pressure), contentDescription = null)

            Text(
                text = "${vitals.bloodPressureRecord}",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFF21F61),
                    textAlign = TextAlign.Center,
                )
            )
        }
    }


}

@Preview(showSystemUi = true)
@Composable
fun PreviewVitalsCard() {
    PanasheCareAssistantTheme {
     //   VitalsCard()
    }
}