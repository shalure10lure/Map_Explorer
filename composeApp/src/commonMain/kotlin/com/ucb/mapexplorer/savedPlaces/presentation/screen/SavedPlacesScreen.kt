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
import com.ucb.designsystem.components.navigation.DsTopAppBar
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.nearbyplaces.domain.model.LugarSavedModel
import com.ucb.mapexplorer.savedPlaces.presentation.state.SavedPlacesEffect
import com.ucb.mapexplorer.savedPlaces.presentation.state.SavedPlacesEvent
import com.ucb.mapexplorer.savedPlaces.presentation.viewmodel.SavedPlacesViewModel
import org.koin.compose.viewmodel.koinViewModel
import com.ucb.mapexplorer.savedPlaces.presentation.composable.EmptyGuardadosContent
import com.ucb.mapexplorer.savedPlaces.presentation.composable.GuardadoItem
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
        DsTopAppBar(
            title = "Ver mis guardados",
            onBackClick = { viewModel.onEvent(SavedPlacesEvent.OnBackClick) },
            backIcon = Icons.AutoMirrored.Filled.ArrowBack,
            actionIcon = Icons.Outlined.Bookmark
        )

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

