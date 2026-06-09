package com.ucb.mapexplorer.social.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel
import com.ucb.mapexplorer.profile.presentation.composable.AvatarDisplay
import com.ucb.mapexplorer.social.presentation.state.SocialPost
import mapexplorer.composeapp.generated.resources.Res
import mapexplorer.composeapp.generated.resources.socialMedia_subtittle_myExperience
import mapexplorer.composeapp.generated.resources.socialMedia_subtittle_sendFriend
import mapexplorer.composeapp.generated.resources.socialMedia_subtittle_viewInMap
import org.jetbrains.compose.resources.stringResource


@Composable
fun SocialPostItem(
    post: SocialPost,
    onAddFriend: (String) -> Unit,
    onViewMap: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // ── Header: avatar + nombre ────────────────────────────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar real del publicador
            val avatarConfig = remember(post.avatarId) {
                if (post.avatarId.isNotBlank()) AvatarConfigModel.fromId(post.avatarId)
                else AvatarConfigModel()
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AppTheme.colors.surface),
                contentAlignment = Alignment.Center
            ) {
                AvatarDisplay(config = avatarConfig, size = 40.dp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = post.userName,
                style = AppTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Imagen del lugar ───────────────────────────────────────────────
        Card(
            modifier  = Modifier.fillMaxWidth().height(220.dp),
            shape     = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors    = CardDefaults.cardColors(containerColor = AppTheme.colors.surface)
        ) {
            if (!post.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model            = post.imageUrl,
                    contentDescription = post.locationName,
                    modifier         = Modifier.fillMaxSize(),
                    contentScale     = ContentScale.Crop
                )
            } else {
                Box(
                    modifier          = Modifier.fillMaxSize().background(AppTheme.colors.surface),
                    contentAlignment  = Alignment.Center
                ) {
                    Text(post.categoryIcon, fontSize = 64.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Info + botón acción ────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = post.locationName,
                    style      = AppTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color      = AppTheme.colors.textPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector        = Icons.Default.Star,
                            contentDescription = null,
                            tint               = if (index < post.rating) Color(0xFFFFC107)
                            else Color.Gray.copy(alpha = 0.3f),
                            modifier           = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text  = post.category,
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary
                    )
                }
            }

            // Botón contextual: "Ver en mapa" si es amigo, "Agregar" si no
            val actionText = when {
                post.isFriend   -> stringResource(Res.string.socialMedia_subtittle_viewInMap)
                post.requestSent -> "Solicitud enviada"
                else            -> stringResource(Res.string.socialMedia_subtittle_sendFriend)
            }
            Text(
                text       = actionText,
                color      = if (post.requestSent) AppTheme.colors.textSecondary
                else Color(0xFF2196F3),
                fontWeight = FontWeight.SemiBold,
                fontSize   = 12.sp,
                modifier   = Modifier.clickable(enabled = !post.requestSent) {
                    if (post.isFriend) onViewMap(post.id) else onAddFriend(post.authorUid)
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Experiencia ────────────────────────────────────────────────────
        Text(
            text       = stringResource(Res.string.socialMedia_subtittle_myExperience),
            style      = AppTheme.typography.bodySmall,
            color      = AppTheme.colors.textSecondary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text  = post.userExperience,
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textPrimary
        )
    }
}