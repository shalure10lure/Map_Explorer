package com.ucb.mapexplorer.nearbyplaces.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel
import com.ucb.mapexplorer.nearbyplaces.presentation.composable.StarRatingRow
@Composable
fun PlaceListItem(
    place: PlaceModel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícono circular de categoría
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(AppTheme.colors.primary.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text     = place.categoryIcon,
                fontSize = 22.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Textos: Tipo, Nombre, Descripción
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = place.category,
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textSecondary
            )
            Text(
                text       = place.name,
                style      = AppTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color      = AppTheme.colors.textPrimary,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            Text(
                text     = place.description.ifBlank { place.category },
                style    = AppTheme.typography.bodySmall,
                color    = AppTheme.colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Estrellas + flecha derecha
        Column(horizontalAlignment = Alignment.End) {
            StarRatingRow(rating = place.rating, starSize = 14.dp)
        }

        Spacer(modifier = Modifier.width(4.dp))

        Icon(
            imageVector     = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint            = AppTheme.colors.textSecondary,
            modifier        = Modifier.size(20.dp)
        )
    }
}