package com.panashecare.assistant.view.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.panashecare.assistant.viewModel.authentication.AuthViewModel

@Composable
fun SignOut(modifier: Modifier = Modifier, authViewModel: AuthViewModel, navigateToLogin: () -> Unit){
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){

        Button(
            onClick = {
                authViewModel.signOut()
                navigateToLogin()
            }
        ) {
            Text("Sign Out")
        }

    }
}