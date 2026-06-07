package com.ucb.mapexplorer.searchUser.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import com.ucb.mapexplorer.friends.domain.model.UserSearchModel
import com.ucb.mapexplorer.searchUser.presentation.state.SearchUserEffect
import com.ucb.mapexplorer.searchUser.presentation.state.SearchUserEvent
import com.ucb.mapexplorer.searchUser.presentation.viewmodel.SearchUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchUserScreen(
    viewModel: SearchUserViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SearchUserEffect.NavigateBack -> onBack()
                is SearchUserEffect.ShowToast ->
                    snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    // Diálogo de confirmación
    if (state.showConfirmationDialog && state.selectedUser != null) {
        val user = state.selectedUser!!
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(SearchUserEvent.OnDismissDialog) },
            containerColor = AppTheme.colors.surface,
            title = {
                Text(
                    when {
                        state.alreadyFriend -> "¡Ya son amigos!"
                        state.requestSent   -> "Solicitud pendiente"
                        else               -> "Enviar solicitud"
                    },
                    color = AppTheme.colors.textPrimary
                )
            },
            text = {
                Text(
                    when {
                        state.alreadyFriend -> "Ya eres amigo de ${user.username}."
                        state.requestSent   -> "Ya enviaste una solicitud a ${user.username}. Espera su respuesta."
                        else               -> "¿Deseas enviar una solicitud de amistad a ${user.username}?"
                    },
                    color = AppTheme.colors.textSecondary
                )
            },
            confirmButton = {
                if (!state.alreadyFriend && !state.requestSent) {
                    if (state.sendingRequest) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = AppTheme.colors.primary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        TextButton(onClick = { viewModel.onEvent(SearchUserEvent.OnConfirmSendRequest) }) {
                            Text("Enviar", color = AppTheme.colors.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(SearchUserEvent.OnDismissDialog) }) {
                    Text(if (state.alreadyFriend || state.requestSent) "Cerrar" else "Cancelar",
                        color = AppTheme.colors.textSecondary)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
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
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.onEvent(SearchUserEvent.OnBackClick) }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = AppTheme.colors.textPrimary
                    )
                }
                Text(
                    text = "Buscar personas",
                    style = AppTheme.typography.headlineLarge.copy(fontSize = 20.sp),
                    color = AppTheme.colors.textPrimary
                )
            }

            // Barra de búsqueda
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onEvent(SearchUserEvent.OnQueryChanged(it)) },
                placeholder = {
                    Text(
                        "Buscar por usuario, correo o ID...",
                        color = AppTheme.colors.textSecondary
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, null, tint = AppTheme.colors.textSecondary)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = AppTheme.colors.surface,
                    unfocusedContainerColor = AppTheme.colors.surface,
                    focusedTextColor = AppTheme.colors.textPrimary,
                    unfocusedTextColor = AppTheme.colors.textPrimary,
                    cursorColor = AppTheme.colors.primary,
                    focusedBorderColor = AppTheme.colors.primary,
                    unfocusedBorderColor = AppTheme.colors.border
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Busca por nombre de usuario, correo o ID",
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textSecondary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AppTheme.colors.primary)
                    }
                }
                state.searchQuery.length >= 2 && state.searchResults.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 48.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "No se encontraron usuarios",
                                color = AppTheme.colors.textSecondary,
                                style = AppTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                state.searchQuery.length < 2 && state.searchResults.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("👥", fontSize = 48.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Escribe al menos 2 caracteres",
                                color = AppTheme.colors.textSecondary,
                                style = AppTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.searchResults, key = { it.uid }) { user ->
                            UserSearchItem(
                                user = user,
                                onClick = { viewModel.onEvent(SearchUserEvent.OnUserSelected(user)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserSearchItem(
    user: UserSearchModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AppTheme.colors.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.username.take(1).uppercase(),
                    color = AppTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.username,
                    style = AppTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = AppTheme.colors.textPrimary
                )
                if (user.description.isNotBlank()) {
                    Text(
                        text = user.description,
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary,
                        maxLines = 1
                    )
                }
            }
            Text(
                text = "Agregar →",
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
