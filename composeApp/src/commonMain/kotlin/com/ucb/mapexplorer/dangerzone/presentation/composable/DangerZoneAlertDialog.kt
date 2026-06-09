package com.ucb.mapexplorer.dangerzone.presentation.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.dangerzone.domain.model.NivelPeligro
import com.ucb.mapexplorer.dangerzone.domain.model.ZonaPeligrosaModel

import mapexplorer.composeapp.generated.resources.Res
import mapexplorer.composeapp.generated.resources.danger_zone_button
import mapexplorer.composeapp.generated.resources.danger_zone_default_desc
import mapexplorer.composeapp.generated.resources.danger_zone_level
import mapexplorer.composeapp.generated.resources.danger_zone_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun DangerZoneAlertDialog(
    zona: ZonaPeligrosaModel?,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = zona != null,
        enter   = fadeIn() + scaleIn(initialScale = 0.85f),
        exit    = fadeOut() + scaleOut(targetScale = 0.85f)
    ) {
        if (zona == null) return@AnimatedVisibility

        val nivelColor = when (zona.nivel) {
            NivelPeligro.BAJO    -> Color(0xFFFFC107)
            NivelPeligro.MEDIO   -> Color(0xFFFF9800)
            NivelPeligro.ALTO    -> Color(0xFFF44336)
            NivelPeligro.CRITICO -> Color(0xFFB71C1C)
        }

        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress       = true,
                dismissOnClickOutside    = false,
                usePlatformDefaultWidth  = false
            )
        ) {
            Card(
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape     = RoundedCornerShape(24.dp),
                colors    = CardDefaults.cardColors(
                    containerColor = AppTheme.colors.surface
                ),
                elevation = CardDefaults.cardElevation(16.dp)
            ) {
                Column(
                    modifier             = Modifier.padding(28.dp),
                    horizontalAlignment  = Alignment.CenterHorizontally,
                    verticalArrangement  = Arrangement.spacedBy(16.dp)
                ) {
                    // Ícono de alerta
                    Box(
                        modifier         = Modifier
                            .size(80.dp)
                            .background(nivelColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text     = zona.nivel.emoji,
                            fontSize = 40.sp
                        )
                    }

                    Text(
                        text       = stringResource(Res.string.danger_zone_title),
                        style      = AppTheme.typography.headlineLarge,
                        color      = nivelColor,
                        fontWeight = FontWeight.Bold,
                        textAlign  = TextAlign.Center
                    )

                    // Nivel badge
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = nivelColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text      = stringResource(Res.string.danger_zone_level, zona.nivel.label),
                            modifier  = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            style     = AppTheme.typography.bodySmall,
                            color     = nivelColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text      = zona.nombre,
                        style     = AppTheme.typography.bodyMedium,
                        color     = AppTheme.colors.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text      = zona.descripcion.ifBlank {
                            stringResource(Res.string.danger_zone_default_desc, zona.tipo)
                        },
                        style     = AppTheme.typography.bodyMedium,
                        color     = AppTheme.colors.textSecondary,
                        textAlign = TextAlign.Center
                    )

                    // Botón entendido
                    Button(
                        onClick  = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = nivelColor
                        )
                    ) {
                        Text(
                            text  = stringResource(Res.string.danger_zone_button),
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
