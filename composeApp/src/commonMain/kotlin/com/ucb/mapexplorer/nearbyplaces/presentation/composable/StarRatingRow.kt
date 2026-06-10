package com.ucb.mapexplorer.nearbyplaces.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StarRatingRow(
    rating: Float,
    starSize: androidx.compose.ui.unit.Dp = 16.dp
) {
    Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
        val filled = rating.toInt().coerceIn(0, 5)
        repeat(filled) {
            Icon(
                imageVector     = Icons.Default.Star,
                contentDescription = null,
                tint            = Color(0xFFFFC107),
                modifier        = Modifier.size(starSize)
            )
        }
        repeat(5 - filled) {
            Icon(
                imageVector     = Icons.Default.Star,
                contentDescription = null,
                tint            = Color(0xFFE0E0E0),
                modifier        = Modifier.size(starSize)
            )
        }
    }
}