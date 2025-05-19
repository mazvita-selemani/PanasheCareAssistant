package com.panashecare.assistant.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panashecare.assistant.model.objects.Shift
import com.panashecare.assistant.ui.theme.PanasheCareAssistantTheme

@Composable
fun ShiftCard(modifier: Modifier = Modifier, shift: Shift, userProfilePicture: Painter? = null, navigateToSingleShiftView: () -> Unit){

    Box(
        modifier = modifier
            .padding(vertical = 10.dp)
            .height(200.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = modifier
                .height(195.dp)
                .fillMaxWidth()
                .background(color = Color(0xFFF8E7FA), shape = RoundedCornerShape(15.dp))

        ){

            Box(modifier = modifier
                .align(Alignment.TopEnd)
                .border(
                    width = 1.dp,
                    color = Color(0xFFF8E7FA),
                    shape = RoundedCornerShape(
                        bottomStart = 15.dp, topEnd = 15.dp
                    )
                )
                .background(
                    color = Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(
                        bottomStart = 15.dp, topEnd = 15.dp
                    )
                ).width(150.dp).height(40.dp).padding(top = 6.dp, bottom = 6.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Duration is ${shift.shiftDuration}",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight(500),
                        color = Color(0xFFFF0000),
                        textAlign = TextAlign.Center,
                    )
                )
            }

            // profile picture
            Box(
                modifier = modifier
                    .align(Alignment.TopStart)
                    .fillMaxHeight(0.75f)
                    .fillMaxWidth(0.45f)
                    .padding(20.dp)
                    .border(
                        width = 3.dp,
                        color = Color(0xFFC911CF),
                        shape = RoundedCornerShape(size = 18.dp)
                    )

            ) {
                if (userProfilePicture != null) {
                    Image(
                        userProfilePicture,
                        contentDescription = null,
                        modifier = modifier
                            .align(Alignment.Center)
                            .padding(2.dp)
                    )
                }

                if( userProfilePicture == null) {
                    Text(
                        text = "Could not load the image",
                        modifier = modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
            }

            //name and start shift details
            Column(modifier= modifier.align(Alignment.CenterEnd).fillMaxWidth(0.6f), verticalArrangement = Arrangement.Center){

                Text(
                    modifier= modifier.padding(8.dp),
                    text = "${shift.healthAideName?.getFullName()}",
                    style = TextStyle(
                        fontSize = 25.sp,
                        fontWeight = FontWeight(600),
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Center,
                    )
                )
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .height(IntrinsicSize.Min)
                ) {
                        Box(
                            modifier = modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(0x1ACF11C9),
                                    shape = RoundedCornerShape(15.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(
                                    text = shift.shiftDate!!,
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight(300),
                                        color = Color(0xFFC911CF),
                                        textAlign = TextAlign.Center,
                                    )
                                )
                                Text(
                                    text = shift.shiftTime!!,
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight(300),
                                        color = Color(0xFFC911CF),
                                    )
                                )
                            }
                        }
                }


            }

        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp)
        ) {
            Button(
                onClick = { navigateToSingleShiftView() },
                modifier = modifier
                    .align(Alignment.BottomEnd)
                    .border(3.dp, Color(0xFFF8E7FA), RoundedCornerShape(12.dp))
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .width(105.dp)
                    .height(45.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFFC911CF)
                ),
                contentPadding = PaddingValues(start = 12.dp, top = 6.dp, end = 12.dp, bottom = 6.dp)
            ) {
                Text(text = "View")
            }
        }

    }


}

@Preview(showBackground = true)
@Composable
fun PreviewshiftCard(){
    PanasheCareAssistantTheme {
   //     shiftCard(isAdmin = false)
    }
}
