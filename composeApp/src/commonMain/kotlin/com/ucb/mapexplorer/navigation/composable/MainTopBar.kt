package com.ucb.mapexplorer.navigation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.navigation.MainTab
import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel
import com.ucb.mapexplorer.profile.presentation.composable.AvatarDisplay
import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainTopBar(
    selectedTab: MainTab,
    avatarConfig: AvatarConfigModel,
    onTabSelected: (MainTab) -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Surface garantiza que el fondo rojo cubra toda la zona, incluyendo debajo de statusBarsPadding
    Surface(
        color = AppTheme.colors.primary,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding() // Empuja el CONTENIDO (iconos) debajo de la barra de estado
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Las 3 pestañas centrales
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TabItem(
                    label    = stringResource(Res.string.navigationBar_socialMedia_textSelection),
                    emoji    = "👥",
                    selected = selectedTab == MainTab.SOCIAL,
                    onClick  = { onTabSelected(MainTab.SOCIAL) }
                )
                TabItem(
                    label    = "Mapa",
                    emoji    = "🗺️",
                    selected = selectedTab == MainTab.MAP,
                    onClick  = { onTabSelected(MainTab.MAP) }
                )
                TabItem(
                    label    = stringResource(Res.string.navigationBar_nearbyPlaces_textSelection),
                    emoji    = "📍",
                    selected = selectedTab == MainTab.NEARBY,
                    onClick  = { onTabSelected(MainTab.NEARBY) }
                )
            }

            // Botón circular del avatar — abre el perfil
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .clickable { onAvatarClick() },
                contentAlignment = Alignment.Center
            ) {
                AvatarDisplay(
                    config   = avatarConfig,
                    size     = 36.dp
                )
            }
        }
    }
}

@Composable
private fun TabItem(
    label: String,
    emoji: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .background(
                if (selected) Color.White.copy(alpha = 0.2f)
                else Color.Transparent
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text  = emoji,
            fontSize = 18.sp
        )
        Text(
            text  = label,
            style = AppTheme.typography.bodySmall.copy(fontSize = 9.sp),
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
        // Indicador de selección
        if (selected) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(2.dp)
                    .background(Color.White, RoundedCornerShape(1.dp))
            )
        }
    }
}
