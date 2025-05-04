package com.panashecare.assistant.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.panashecare.assistant.R

@Composable
fun SearchBar(modifier: Modifier = Modifier, value: String = "", onSearchChange: () -> Unit = {}) {

    OutlinedTextField(
        modifier = modifier
            .border(
                width = 0.5.dp,
                color = Color(0xFF777777),
                shape = RoundedCornerShape(30.dp)
            )
            .width(354.dp)
            .height(60.dp),
        value = value,
        onValueChange = { onSearchChange() },
        placeholder = { Text("Search Here") },
        leadingIcon = {
            Image(
                painter = painterResource(R.drawable.search_1),
                contentDescription = null,
                modifier = Modifier
                    .size(25.dp)
                    .padding(start = 4.dp)
            )
        },
        shape = RoundedCornerShape(30.dp)
    )
}

@Preview
@Composable
fun PreviewSearchBar() {
    SearchBar()
}