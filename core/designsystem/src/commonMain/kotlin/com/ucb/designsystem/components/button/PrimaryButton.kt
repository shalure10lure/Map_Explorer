package com.ucb.designsystem.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ucb.designsystem.theme.AppTheme

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    isPrimary: Boolean = true
) {
    val containerColor = if (isPrimary) AppTheme.colors.secondary else Color.Transparent
    val contentColor = if (isPrimary) Color.White else AppTheme.colors.secondary
    val borderColor = if (enabled) AppTheme.colors.secondary else AppTheme.colors.secondary.copy(alpha = 0.3f)

    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = if (isPrimary) AppTheme.colors.secondary.copy(alpha = 0.12f) else Color.Transparent
        ),
        border = if (isPrimary) null else BorderStroke(1.dp, borderColor)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = contentColor
            )
        } else {
            Text(
                text = text,
                style = AppTheme.typography.labelLarge,
                color = contentColor
            )
        }
    }
}
