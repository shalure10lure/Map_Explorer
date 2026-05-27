package com.ucb.mapexplorer.profile.presentation.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.auth.presentation.register.state.AvatarTab
import com.ucb.mapexplorer.profile.domain.model.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun AvatarEditorCompact(
    avatarConfig: AvatarConfigModel,
    selectedTab: AvatarTab,
    onBodySelected: (AvatarBody) -> Unit,
    onHatSelected: (AvatarHat) -> Unit,
    onAccSelected: (AvatarAccessory) -> Unit,
    onTabSelected: (AvatarTab) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        // Preview del avatar en grande
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AppTheme.colors.surface),
            contentAlignment = Alignment.Center
        ) {
            AvatarDisplay(
                config = avatarConfig,
                size = 140.dp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs de categoría
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AvatarTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTabSelected(tab) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) AppTheme.colors.primary
                    else AppTheme.colors.surface,
                    contentColor = if (isSelected) Color.White
                    else AppTheme.colors.textPrimary
                ) {
                    Text(
                        text = tab.label,
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = AppTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Opciones según tab
        when (selectedTab) {

            AvatarTab.BODY -> {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(AvatarBody.entries) { body ->
                        AvatarPartItem(
                            resource   = body.toResource(),
                            isSelected = avatarConfig.body == body,
                            onClick    = { onBodySelected(body) }
                        )
                    }
                }
            }

            AvatarTab.HAT -> {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Opción sin sombrero
                    item {
                        NonePartItem(
                            isSelected = avatarConfig.hat == AvatarHat.NONE,
                            onClick    = { onHatSelected(AvatarHat.NONE) }
                        )
                    }
                    items(AvatarHat.entries.filter { it != AvatarHat.NONE }) { hat ->
                        hat.toResource()?.let { res ->
                            AvatarPartItem(
                                resource   = res,
                                isSelected = avatarConfig.hat == hat,
                                onClick    = { onHatSelected(hat) }
                            )
                        }
                    }
                }
            }

            AvatarTab.ACCESSORY -> {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        NonePartItem(
                            isSelected = avatarConfig.accessory == AvatarAccessory.NONE,
                            onClick    = { onAccSelected(AvatarAccessory.NONE) }
                        )
                    }
                    items(AvatarAccessory.entries.filter { it != AvatarAccessory.NONE }) { acc ->
                        acc.toResource()?.let { res ->
                            AvatarPartItem(
                                resource   = res,
                                isSelected = avatarConfig.accessory == acc,
                                onClick    = { onAccSelected(acc) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AvatarPartItem(
    resource: DrawableResource,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(AppTheme.colors.surface)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) AppTheme.colors.primary
                else AppTheme.colors.border,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(resource),
            contentDescription = null,
            modifier = Modifier.size(54.dp)
        )
    }
}

@Composable
private fun NonePartItem(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(AppTheme.colors.surface)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) AppTheme.colors.primary
                else AppTheme.colors.border,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            "✕",
            style = AppTheme.typography.headlineLarge,
            color = AppTheme.colors.textSecondary
        )
    }
}