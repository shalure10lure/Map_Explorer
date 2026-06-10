package com.ucb.designsystem.components.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.designsystem.theme.AppTheme

@Composable
fun DsTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    backIcon: ImageVector,
    modifier: Modifier = Modifier,
    actionIcon: ImageVector? = null,
    onActionClick: (() -> Unit)? = null,
    actionTint: androidx.compose.ui.graphics.Color = AppTheme.colors.primary
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = backIcon,
            contentDescription = "Volver",
            tint = AppTheme.colors.textPrimary,
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() }
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = AppTheme.typography.headlineLarge.copy(fontSize = 20.sp),
            color = AppTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
        
        if (actionIcon != null) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = actionIcon,
                contentDescription = null,
                tint = actionTint,
                modifier = Modifier
                    .size(24.dp)
                    .then(if (onActionClick != null) Modifier.clickable { onActionClick() } else Modifier)
            )
        }
    }
}
