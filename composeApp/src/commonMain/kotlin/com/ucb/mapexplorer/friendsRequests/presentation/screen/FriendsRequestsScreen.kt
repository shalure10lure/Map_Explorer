package com.ucb.mapexplorer.friendsRequests.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.friendsRequests.presentation.state.FriendsRequestsEffect
import com.ucb.mapexplorer.friendsRequests.presentation.state.FriendsRequestsEvent
import com.ucb.mapexplorer.friendsRequests.presentation.viewmodel.FriendsRequestsViewModel

import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun FriendsRequestsScreen(
    viewModel: FriendsRequestsViewModel,
    onBack: () -> Unit,
    onNavigateToSearch: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                FriendsRequestsEffect.NavigateBack -> onBack()
                is FriendsRequestsEffect.ShowToast -> { }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        // Header Rojo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFC62828)) 
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(Res.string.buttonText_viewFriendRequest),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Subheader con botón de búsqueda
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { viewModel.onEvent(FriendsRequestsEvent.OnBackClick) }) {
                Text(
                    text = "← ${stringResource(Res.string.navigationSelector_backToSocialMedia)}",
                    fontSize = 14.sp,
                    color = AppTheme.colors.textPrimary
                )
            }

            Button(
                onClick = onNavigateToSearch,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                Spacer(Modifier.width(4.dp))
                Text("Enviar solicitud", fontSize = 12.sp, color = Color.White)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            if (state.requests.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No tienes solicitudes pendientes", 
                            color = AppTheme.colors.textSecondary,
                            style = AppTheme.typography.bodyMedium
                        )
                    }
                }
            }
            items(state.requests) { request ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(AppTheme.colors.surface, shape = CircleShape)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar (Círculo con inicial)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(AppTheme.colors.border.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val initial = if (request.isNotEmpty()) request.first().uppercase() else "?"
                        Text(
                            text = initial,
                            fontSize = 14.sp,
                            color = AppTheme.colors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = request,
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Medium,
                        color = AppTheme.colors.textPrimary
                    )

                    // Botón Rechazar (X)
                    IconButton(onClick = { viewModel.onEvent(FriendsRequestsEvent.OnDeclineClick(request)) }) {
                        Text("✕", color = Color.Red, fontWeight = FontWeight.Bold)
                    }

                    // Botón Aceptar (Check)
                    IconButton(onClick = { viewModel.onEvent(FriendsRequestsEvent.OnAcceptClick(request)) }) {
                        Text("✓", color = Color(0xFF2196F3), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
