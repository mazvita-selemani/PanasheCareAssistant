import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panashecare.assistant.R
import com.panashecare.assistant.ui.theme.PanasheCareAssistantTheme

@Composable
fun MedicationDetailsCard(modifier: Modifier = Modifier) {
    var checked by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .padding(25.dp)
            .fillMaxWidth()
            .height(71.dp)
            .background(color = Color(0xFFE7F7FA), shape = RoundedCornerShape(18.dp))
            .padding(horizontal = 13.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = modifier
                .padding(end = 10.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(25.dp)
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.solar_pills_bold),
                contentDescription = null
            )
        }

        Column {
            Text(
                text = "Nurofen",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight(400),
                    textAlign = TextAlign.Center,
                )
            )
            Text(
                text = "15 mg",
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFF21F61),
                    textAlign = TextAlign.Center,
                )
            )
        }

        Spacer(modifier = modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .border(
                    width = if (checked) 3.dp else 1.dp,
                    color = Color.Black,
                    shape = CircleShape
                )
                .clickable { checked = !checked },
            contentAlignment = Alignment.Center
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { checked = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.Transparent,
                    uncheckedColor = Color.Transparent,
                    checkmarkColor = Color.Black
                ),
                modifier = Modifier
                    .size(20.dp) // match the inner area size
                    .background(Color.White, CircleShape)
            )
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun PreviewMedicationDetailsCard(){
    PanasheCareAssistantTheme {
        MedicationDetailsCard()
    }
}