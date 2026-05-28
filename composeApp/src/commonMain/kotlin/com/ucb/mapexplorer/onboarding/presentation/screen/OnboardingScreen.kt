package com.ucb.mapexplorer.onboarding.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.navigation.NavController
import com.ucb.designsystem.components.button.PrimaryButton
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.navigation.NavRoute
import com.ucb.mapexplorer.onboarding.presentation.viewmodel.OnboardingEvent
import com.ucb.mapexplorer.onboarding.presentation.viewmodel.OnboardingViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Navegar al Home cuando corresponda
    LaunchedEffect(state.navigateToHome) {
        if (state.navigateToHome) {
            navController.navigate(NavRoute.Map) {
                popUpTo(NavRoute.Onboarding) { inclusive = true }
            }
        }
    }

    if (state.isLoading) {
        Box(
            Modifier
                .fillMaxSize()
                .background(AppTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = AppTheme.colors.primary)
        }
        return
    }

    val page    = state.pages.getOrNull(state.currentIndex) ?: return
    val isFirst = state.currentIndex == 0
    val isLast  = state.currentIndex == state.pages.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                state.pages.indices.forEach { i ->
                    Box(
                        modifier = Modifier
                            .size(if (i == state.currentIndex) 10.dp else 7.dp)
                            .background(
                                color = if (i == state.currentIndex)
                                    AppTheme.colors.primary
                                else
                                    AppTheme.colors.textSecondary.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(50)
                            )
                    )
                }
            }

            TextButton(onClick = { viewModel.onEvent(OnboardingEvent.Skip) }) {
                Text(
                    text = "Omitir",
                    color = AppTheme.colors.textSecondary,
                    style = AppTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "MAP EXPLORER",
            style = AppTheme.typography.headlineLarge,
            color = AppTheme.colors.textPrimary
        )

        Spacer(Modifier.height(16.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = AppTheme.colors.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = page.title,
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textPrimary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = page.description,
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textSecondary
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        AsyncImage(
            model = page.imageUrl,
            contentDescription = page.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.weight(1f))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isFirst) {
                OutlinedButton(
                    onClick = { viewModel.onEvent(OnboardingEvent.Back) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppTheme.colors.primary
                    )
                ) {
                    Text("Anterior")
                }
            }

            PrimaryButton(
                text = if (isLast) "INICIAR" else "SIGUIENTE",
                onClick = {
                    if (isLast) viewModel.onEvent(OnboardingEvent.Start)
                    else        viewModel.onEvent(OnboardingEvent.Next)
                },
                modifier = Modifier.weight(1f),
                isPrimary = true
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}