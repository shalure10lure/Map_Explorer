package com.ucb.mapexplorer.friendsRequests.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.friends.domain.model.FriendRequestModel
import com.ucb.mapexplorer.friendsRequests.presentation.state.FriendsRequestsEffect
import com.ucb.mapexplorer.friendsRequests.presentation.state.FriendsRequestsEvent
import com.ucb.mapexplorer.friendsRequests.presentation.viewmodel.FriendsRequestsViewModel
import com.ucb.mapexplorer.friendsRequests.presentation.composable.FriendRequestItem


@Composable
fun FriendsRequestsScreen(
    viewModel: FriendsRequestsViewModel,
    onBack: () -> Unit,
    onNavigateToSearch: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                FriendsRequestsEffect.NavigateBack -> onBack()
                FriendsRequestsEffect.NavigateToSearch -> onNavigateToSearch()
                is FriendsRequestsEffect.ShowToast ->
                    snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AppTheme.colors.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppTheme.colors.background)
        ) {
            // ── Header rojo ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppTheme.colors.primary)
                    .statusBarsPadding()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Solicitudes de Amistad",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // ── Subheader ────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { viewModel.onEvent(FriendsRequestsEvent.OnBackClick) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = AppTheme.colors.textPrimary
                    )
                }
                Button(
                    onClick = onNavigateToSearch,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppTheme.colors.primary
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Buscar personas", fontSize = 12.sp, color = Color.White)
                }
            }

            HorizontalDivider(
                color = AppTheme.colors.border.copy(alpha = 0.3f),
                thickness = 0.5.dp
            )

            // ── Contenido ───────────────────────────────────────────────
            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AppTheme.colors.primary)
                    }
                }

                state.requests.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("👥", fontSize = 56.sp)
                            Text(
                                "No tienes solicitudes pendientes",
                                style = AppTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = AppTheme.colors.textPrimary
                            )
                            Text(
                                "Busca personas para enviarles\nuna solicitud de amistad",
                                style = AppTheme.typography.bodySmall,
                                color = AppTheme.colors.textSecondary
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.requests, key = { it.requestId }) { request ->
                            FriendRequestItem(
                                request = request,
                                onAccept = {
                                    viewModel.onEvent(FriendsRequestsEvent.OnAcceptClick(request))
                                },
                                onDecline = {
                                    viewModel.onEvent(FriendsRequestsEvent.OnDeclineClick(request))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
