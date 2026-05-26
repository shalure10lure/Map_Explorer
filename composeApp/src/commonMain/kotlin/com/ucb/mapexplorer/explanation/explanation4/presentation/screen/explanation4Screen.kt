package com.ucb.mapexplorer.explanation.explanation4.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ucb.designsystem.components.button.PrimaryButton
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.navigation.NavRoute
import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun Explanation4Screen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "MAP EXPLORER",
            style = AppTheme.typography.headlineLarge,
            color = AppTheme.colors.textPrimary
        )

        Spacer(modifier = Modifier.height(24.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = AppTheme.colors.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(Res.string.presentation_subtittle_exploreWithSafety),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.presentation_descriptionPart2_exploreWithSafety),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))

        Image(
            painter = painterResource(Res.drawable.logo_map_explorer),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(240.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = stringResource(Res.string.login_buttonText_login).uppercase(),
            onClick = {
                // Al terminar las explicaciones, llevar al mapa
                navController.navigate(NavRoute.Map) {
                    popUpTo(NavRoute.Explanation1) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            isPrimary = true
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}
