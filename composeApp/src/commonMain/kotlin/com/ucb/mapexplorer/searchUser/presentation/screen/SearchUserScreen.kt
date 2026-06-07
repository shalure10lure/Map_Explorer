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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.searchUser.presentation.state.SearchUserEvent
import com.ucb.mapexplorer.searchUser.presentation.viewmodel.SearchUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchUserScreen(
    viewModel: SearchUserViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    if (state.showConfirmationDialog && state.selectedUser != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(SearchUserEvent.OnDismissDialog) },
            title = { Text("Enviar solicitud", color = AppTheme.colors.textPrimary) },
            text = { Text("¿Deseas enviar una solicitud de amistad a ${state.selectedUser?.username}?", color = AppTheme.colors.textPrimary) },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(SearchUserEvent.OnConfirmSendRequest) }) {
                    Text("Aceptar", color = Color(0xFF2196F3))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(SearchUserEvent.OnDismissDialog) }) {
                    Text("Cancelar", color = AppTheme.colors.error)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = AppTheme.colors.surface // Corregido: Ahora se adapta al tema
        )
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
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = AppTheme.colors.textPrimary
                )
            }
            Text(
                text = "Buscar Usuario",
                style = AppTheme.typography.headlineLarge.copy(fontSize = 20.sp),
                color = AppTheme.colors.textPrimary
            )
        }

        // Search Bar
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.onEvent(SearchUserEvent.OnQueryChanged(it)) },
            placeholder = { Text("Nombre de usuario...", color = AppTheme.colors.textSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AppTheme.colors.textSecondary) },
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

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppTheme.colors.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.searchResults.isEmpty() && state.searchQuery.isNotEmpty()) {
                    item {
                        Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No se encontraron usuarios", color = AppTheme.colors.textSecondary)
                        }
                    }
                }
                items(state.searchResults) { user ->
                    UserSearchItem(
                        username = user.username,
                        onClick = { viewModel.onEvent(SearchUserEvent.OnUserSelected(user)) }
                    )
                }
            }
        }
    }
}

@Composable
fun UserSearchItem(
    username: String,
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
                    .size(40.dp)
                    .background(AppTheme.colors.border.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = username.take(1).uppercase(),
                    color = AppTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = username,
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
