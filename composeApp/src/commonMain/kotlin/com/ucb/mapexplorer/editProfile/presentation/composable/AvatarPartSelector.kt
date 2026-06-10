package com.ucb.mapexplorer.editProfile.presentation.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ucb.designsystem.theme.AppTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun AvatarPartSelector(
    items: List<Pair<String, DrawableResource>>,
    selectedName: String,
    onSelect: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { (name, resource) ->
            val isSelected = name == selectedName
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppTheme.colors.surface)
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.border,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelect(name) },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(resource),
                    contentDescription = name,
                    modifier = Modifier.size(60.dp)
                )
            }
        }
    }
}