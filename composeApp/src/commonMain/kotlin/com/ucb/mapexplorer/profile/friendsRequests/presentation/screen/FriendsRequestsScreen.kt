package com.ucb.mapexplorer.profile.friendsRequests.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.ucb.mapexplorer.profile.friendsRequests.presentation.state.FriendsRequestsEffect
import com.ucb.mapexplorer.profile.friendsRequests.presentation.state.FriendsRequestsEvent
import com.ucb.mapexplorer.profile.friendsRequests.presentation.viewmodel.FriendsRequestsViewModel

import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun FriendsRequestsScreen(
    viewModel: FriendsRequestsViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                FriendsRequestsEffect.NavigateBack -> onBack()
                is FriendsRequestsEffect.ShowToast -> {
                    // Mostrar mensaje de éxito
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F4FF)) // Fondo lavanda claro como en la imagen
    ) {
        // Header Rojo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFC62828)) // Rojo oscuro
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

        // Subheader Volver
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { viewModel.onEvent(FriendsRequestsEvent.OnBackClick) }) {
                Text(
                    text = "← ${stringResource(Res.string.navigationSelector_backToSocialMedia)}",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            items(state.requests) { request ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color.White, shape = CircleShape)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar inicial (Círculo púrpura)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFEDE7F6), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = request.last().toString(),
                            fontSize = 14.sp,
                            color = Color(0xFF673AB7)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = request,
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Medium
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
