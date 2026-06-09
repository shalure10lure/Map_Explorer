package com.ucb.mapexplorer.favoritePlaces.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.designsystem.components.navigation.DsTopAppBar
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.favoritePlaces.presentation.state.FavoritePlacesEffect
import com.ucb.mapexplorer.favoritePlaces.presentation.state.FavoritePlacesEvent
import com.ucb.mapexplorer.favoritePlaces.presentation.viewmodel.FavoritePlacesViewModel
import com.ucb.mapexplorer.nearbyplaces.domain.model.LugarSavedModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavoritePlacesScreen(
    onBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: FavoritePlacesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFavoritos()
        viewModel.effect.collect { effect ->
            when (effect) {
                FavoritePlacesEffect.NavigateBack -> onBack()
                is FavoritePlacesEffect.NavigateToPlaceDetail ->
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
        DsTopAppBar(
            title = "Lugares Favoritos",
            onBackClick = { viewModel.onEvent(FavoritePlacesEvent.OnBackClick) },
            backIcon = Icons.AutoMirrored.Filled.ArrowBack,
            actionIcon = Icons.Default.Favorite
        )

        HorizontalDivider(color = AppTheme.colors.border.copy(alpha = 0.3f), thickness = 0.5.dp)

        // ── Contenido ─────────────────────────────────────────────────────
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppTheme.colors.primary)
                }
            }

            state.favoritos.isEmpty() -> {
                EmptyFavoritosContent()
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(state.favoritos, key = { it.lugarId }) { lugar ->
                        FavoritoItem(
                            lugar = lugar,
                            onClick = {
                                viewModel.onEvent(FavoritePlacesEvent.OnPlaceClick(lugar.lugarId))
                            },
                            onRemove = {
                                viewModel.onEvent(FavoritePlacesEvent.OnRemoveFavorito(lugar.lugarId))
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
private fun FavoritoItem(
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
        // Ícono categoría
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

        // Botón quitar de favoritos
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Quitar de favoritos",
                tint = AppTheme.colors.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun EmptyFavoritosContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("❤️", fontSize = 56.sp)
            Text(
                "Aún no tienes favoritos",
                style = AppTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.textPrimary
            )
            Text(
                "Marca lugares como favoritos\ndesde el detalle del lugar",
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textSecondary
            )
        }
    }
}