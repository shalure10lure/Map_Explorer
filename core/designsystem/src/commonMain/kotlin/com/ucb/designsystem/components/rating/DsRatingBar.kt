package com.ucb.designsystem.components.rating

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DsRatingBar(
    rating: Int,
    starIcon: ImageVector,
    modifier: Modifier = Modifier,
    onRatingSelected: ((Int) -> Unit)? = null,
    maxRating: Int = 5,
    starSize: Dp = 44.dp,
    activeColor: Color = Color(0xFFFFC107),
    inactiveColor: Color = Color(0xFFE0E0E0)
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        (1..maxRating).forEach { star ->
            Icon(
                imageVector = starIcon,
                contentDescription = "$star estrellas",
                tint = if (star <= rating) activeColor else inactiveColor,
                modifier = Modifier
                    .size(starSize)
                    .then(
                        if (onRatingSelected != null) {
                            Modifier.clickable { onRatingSelected(star) }
                        } else {
                            Modifier
                        }
                    )
            )
        }
    }
}
