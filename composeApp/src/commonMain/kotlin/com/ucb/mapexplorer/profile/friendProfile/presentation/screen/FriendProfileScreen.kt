package com.ucb.mapexplorer.profile.friendProfile.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.designsystem.components.button.PrimaryButton
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.profile.friendProfile.presentation.state.FriendProfileEffect
import com.ucb.mapexplorer.profile.friendProfile.presentation.state.FriendProfileEvent
import com.ucb.mapexplorer.profile.friendProfile.presentation.viewmodel.FriendProfileViewModel
import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun FriendProfileScreen(
    viewModel: FriendProfileViewModel,
    onBack: () -> Unit,
    onBackToProfile: () -> Unit,
    onTrackRealtime: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                FriendProfileEffect.NavigateBack -> onBack()
                FriendProfileEffect.NavigateToOwnProfile -> onBackToProfile()
                FriendProfileEffect.StartRealtimeTracking -> onTrackRealtime()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { viewModel.onEvent(FriendProfileEvent.OnBackClick) }) {
                Text(
                    text = "← ${stringResource(Res.string.navigationSelector_backToMap)}",
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textPrimary
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.friendName,
                        style = AppTheme.typography.headlineLarge,
                        color = AppTheme.colors.textPrimary
                    )
                    Text(
                        text = stringResource(Res.string.profileView_subtittle_description),
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary
                    )
                    Text(
                        text = state.description,
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nivel: ${state.level}",
                        style = AppTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AppTheme.colors.textPrimary
                    )
                    Text(
                        text = "${stringResource(Res.string.profileView_subtittle_email)}: ${state.email}",
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary
                    )
                }

                // Avatar Placeholder
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(AppTheme.colors.surface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Personaje\nAmigo",
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(Res.string.profileView_subtittle_friends),
                modifier = Modifier.align(Alignment.Start),
                style = AppTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = AppTheme.colors.surface
                )
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .padding(8.dp)
                ) {
                    items(state.mutualFriends) { friend ->
                        Text(
                            text = friend,
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.textPrimary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PrimaryButton(
                    text = stringResource(Res.string.buttonText_backToProfile),
                    onClick = { viewModel.onEvent(FriendProfileEvent.OnBackToProfileClick) },
                    modifier = Modifier.weight(1f),
                    isPrimary = true
                )
                PrimaryButton(
                    text = "Rastrear",
                    onClick = { viewModel.onEvent(FriendProfileEvent.OnTrackRealtimeClick) },
                    modifier = Modifier.weight(1f),
                    isPrimary = true
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
