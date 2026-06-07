package com.ucb.mapexplorer.friendProfile.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.designsystem.components.button.PrimaryButton
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.friendProfile.presentation.state.FriendProfileEffect
import com.ucb.mapexplorer.friendProfile.presentation.state.FriendProfileEvent
import com.ucb.mapexplorer.friendProfile.presentation.viewmodel.FriendProfileViewModel
import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel
import com.ucb.mapexplorer.profile.presentation.composable.AvatarDisplay

@Composable
fun FriendProfileScreen(
    friendUid: String,
    viewModel: FriendProfileViewModel,
    onBack: () -> Unit,
    onBackToProfile: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(friendUid) {
        viewModel.loadFriend(friendUid)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                FriendProfileEffect.NavigateBack -> onBack()
                FriendProfileEffect.NavigateToOwnProfile -> onBackToProfile()
                FriendProfileEffect.FriendRemoved -> onBackToProfile()
                is FriendProfileEffect.ShowToast ->
                    snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    // Diálogo confirmar romper amistad
    if (state.showRemoveDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(FriendProfileEvent.OnDismissRemoveDialog) },
            containerColor = AppTheme.colors.surface,
            title = {
                Text("¿Romper amistad?", color = AppTheme.colors.textPrimary)
            },
            text = {
                Text(
                    "¿Seguro que quieres eliminar a ${state.friendName} de tus amigos?",
                    color = AppTheme.colors.textSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(FriendProfileEvent.OnConfirmRemove) }) {
                    Text("Sí, eliminar", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(FriendProfileEvent.OnDismissRemoveDialog) }) {
                    Text("Cancelar", color = AppTheme.colors.textSecondary)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AppTheme.colors.background
    ) { padding ->
        if (state.isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AppTheme.colors.primary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppTheme.colors.background)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = AppTheme.colors.textPrimary,
                    modifier = Modifier.size(22.dp).clickable { onBack() }
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Perfil de amigo",
                    style = AppTheme.typography.headlineLarge.copy(fontSize = 18.sp),
                    color = AppTheme.colors.textPrimary
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))

                // Avatar + datos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.friendName.ifBlank { "Usuario" },
                            style = AppTheme.typography.headlineLarge,
                            color = AppTheme.colors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Descripción",
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colors.textSecondary
                        )
                        Text(
                            text = state.description.ifBlank { "Sin descripción" },
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.textPrimary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Nivel: ${state.level}",
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.textPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Avatar (usa AvatarConfigModel si tienes avatar_id guardado)
                    val avatarConfig = remember(state.avatarId) {
                        if (state.avatarId.isNotBlank())
                            AvatarConfigModel.fromId(state.avatarId)
                        else AvatarConfigModel()
                    }
                    AvatarDisplay(config = avatarConfig, size = 90.dp)
                }

                Spacer(Modifier.height(24.dp))

                // Amigos en común
                if (state.mutualFriends.isNotEmpty()) {
                    Text(
                        text = "Amigos en común (${state.mutualFriends.size})",
                        modifier = Modifier.align(Alignment.Start),
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
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
                                .heightIn(max = 120.dp)
                                .padding(8.dp)
                        ) {
                            items(state.mutualFriends) { name ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(AppTheme.colors.primary.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            name.take(1).uppercase(),
                                            color = AppTheme.colors.primary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = name,
                                        style = AppTheme.typography.bodyMedium,
                                        color = AppTheme.colors.textPrimary
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Lista de amigos del usuario
                Text(
                    text = "Sus amigos (${state.friendsList.size})",
                    modifier = Modifier.align(Alignment.Start),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = AppTheme.colors.surface
                    )
                ) {
                    if (state.friendsList.isEmpty()) {
                        Text(
                            "Aún no tiene amigos",
                            modifier = Modifier.padding(16.dp),
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colors.textSecondary
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 160.dp)
                                .padding(8.dp)
                        ) {
                            items(state.friendsList) { friend ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(AppTheme.colors.primary.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            friend.username.take(1).uppercase(),
                                            color = AppTheme.colors.primary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        text = friend.username,
                                        style = AppTheme.typography.bodyMedium,
                                        color = AppTheme.colors.textPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PrimaryButton(
                        text = "Volver al perfil",
                        onClick = { viewModel.onEvent(FriendProfileEvent.OnBackToProfileClick) },
                        modifier = Modifier.weight(1f),
                        isPrimary = true
                    )
                    OutlinedButton(
                        onClick = { viewModel.onEvent(FriendProfileEvent.OnRemoveFriendClick) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !state.isRemoving,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        if (state.isRemoving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.Red,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Romper amistad", color = Color.Red)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
