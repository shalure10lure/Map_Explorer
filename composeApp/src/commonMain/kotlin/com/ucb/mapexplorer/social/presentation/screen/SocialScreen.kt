package com.ucb.mapexplorer.social.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ucb.designsystem.theme.AppTheme

@Composable
fun SocialScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Espacio Social",
            style = AppTheme.typography.headlineLarge,
            color = AppTheme.colors.textPrimary
        )
    }
}