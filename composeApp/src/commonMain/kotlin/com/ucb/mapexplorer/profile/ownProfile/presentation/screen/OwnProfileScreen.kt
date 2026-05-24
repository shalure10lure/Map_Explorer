package com.ucb.mapexplorer.profile.ownProfile.presentation.screen

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
import com.ucb.mapexplorer.profile.ownProfile.presentation.state.OwnProfileEffect
import com.ucb.mapexplorer.profile.ownProfile.presentation.state.OwnProfileEvent
import com.ucb.mapexplorer.profile.ownProfile.presentation.viewmodel.OwnProfileViewModel
import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun OwnProfileScreen(
    viewModel: OwnProfileViewModel,
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onViewRequests: () -> Unit,
    onViewFriend: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OwnProfileEffect.NavigateBack -> onBack()
                OwnProfileEffect.NavigateToEditProfile -> onEditProfile()
                OwnProfileEffect.NavigateToRequests -> onViewRequests()
                is OwnProfileEffect.NavigateToFriendProfile -> onViewFriend(effect.friendName)
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
            TextButton(onClick = { viewModel.onEvent(OwnProfileEvent.OnBackClick) }) {
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
                        text = state.userName,
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
                        color = AppTheme.colors.textPrimary,
                        fontWeight = FontWeight.SemiBold
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
                        "Personaje\nusuario", 
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
                color = AppTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
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
                    items(state.friends) { friend ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = friend,
                                style = AppTheme.typography.bodyMedium,
                                color = AppTheme.colors.textPrimary
                            )
                            TextButton(onClick = { viewModel.onEvent(OwnProfileEvent.OnFriendClick(friend)) }) {
                                Text(
                                    "Ver Perfil >", 
                                    style = AppTheme.typography.bodySmall,
                                    color = AppTheme.colors.primary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PrimaryButton(
                    text = stringResource(Res.string.buttonText_editProfile),
                    onClick = { viewModel.onEvent(OwnProfileEvent.OnEditProfileClick) },
                    modifier = Modifier.weight(1f),
                    isPrimary = true
                )
                PrimaryButton(
                    text = stringResource(Res.string.buttonText_viewFriendRequest),
                    onClick = { viewModel.onEvent(OwnProfileEvent.OnViewRequestsClick) },
                    modifier = Modifier.weight(1f),
                    isPrimary = true
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
