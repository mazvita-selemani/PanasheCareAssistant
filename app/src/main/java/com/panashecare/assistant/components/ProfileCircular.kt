package com.panashecare.assistant.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.panashecare.assistant.R

/**
 * reusable circular profile picture component
 */
@Composable
fun ProfileCircular(
    modifier: Modifier = Modifier,
    profileResourceId: Int? = null,
    profilePictureSize: Int = 55,
    innerPadding: Int = 5,
    navigateToProfile: () -> Unit
) {
    Box(
        modifier = modifier
            .size(profilePictureSize.dp)
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = CircleShape
            )
            .padding(innerPadding.dp).clickable { navigateToProfile() }
    ) {
        if (profileResourceId == null) {
            Image(
                painter = painterResource(id = R.drawable.iconamoon_profile_thin),
                contentDescription = "Profile",
                modifier = modifier.fillMaxSize()
            )
        }

        if (profileResourceId != null) {
            Image(
                painter = painterResource(id = profileResourceId),
                contentDescription = "Profile",
                modifier = modifier.fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
fun PreviewProfileCircular() {
  //  ProfileCircular()
}