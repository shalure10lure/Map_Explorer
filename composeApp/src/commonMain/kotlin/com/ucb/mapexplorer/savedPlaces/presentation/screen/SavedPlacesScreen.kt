package com.ucb.mapexplorer.savedPlaces.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.nearbyplaces.domain.model.LugarSavedModel
import com.ucb.mapexplorer.savedPlaces.presentation.state.SavedPlacesEffect
import com.ucb.mapexplorer.savedPlaces.presentation.state.SavedPlacesEvent
import com.ucb.mapexplorer.savedPlaces.presentation.viewmodel.SavedPlacesViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SavedPlacesScreen(
    onBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: SavedPlacesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadGuardados()
        viewModel.effect.collect { effect ->
            when (effect) {
                SavedPlacesEffect.NavigateBack -> onBack()
                is SavedPlacesEffect.NavigateToPlaceDetail ->
                    onNavigateToDetail(effect.lugarId)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        // ── Header ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = AppTheme.colors.textPrimary,
                modifier = Modifier.size(22.dp).clickable { viewModel.onEvent(SavedPlacesEvent.OnBackClick) }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Ver mis guardados",
                style = AppTheme.typography.headlineLarge.copy(fontSize = 20.sp),
                color = AppTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Outlined.Bookmark,
                contentDescription = null,
                tint = AppTheme.colors.primary,
                modifier = Modifier.size(22.dp)
            )
        }

        HorizontalDivider(color = AppTheme.colors.border.copy(alpha = 0.3f), thickness = 0.5.dp)

        // ── Contenido ─────────────────────────────────────────────────────
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppTheme.colors.primary)
                }
            }

            state.guardados.isEmpty() -> {
                EmptyGuardadosContent()
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(state.guardados, key = { it.lugarId }) { lugar ->
                        GuardadoItem(
                            lugar = lugar,
                            onClick = {
                                viewModel.onEvent(SavedPlacesEvent.OnPlaceClick(lugar.lugarId))
                            },
                            onRemove = {
                                viewModel.onEvent(SavedPlacesEvent.OnRemoveGuardado(lugar.lugarId))
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = AppTheme.colors.border.copy(alpha = 0.3f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GuardadoItem(
    lugar: LugarSavedModel,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(AppTheme.colors.primary.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = lugar.iconoCategoria, fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lugar.nombre,
                style = AppTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = lugar.categoria,
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Bookmark,
                contentDescription = "Quitar de guardados",
                tint = AppTheme.colors.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun EmptyGuardadosContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("🔖", fontSize = 56.sp)
            Text(
                "Aún no tienes guardados",
                style = AppTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.textPrimary
            )
            Text(
                "Guarda lugares para visitarlos\nmás tarde desde el detalle",
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textSecondary
            )
        }
    }
}