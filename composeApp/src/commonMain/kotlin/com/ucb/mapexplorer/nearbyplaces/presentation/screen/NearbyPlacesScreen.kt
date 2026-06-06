package com.ucb.mapexplorer.nearbyplaces.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.map.presentation.viewmodel.MapViewModel
import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel
import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyPlacesScreen(
    mapViewModel: MapViewModel,
    viewModel: com.ucb.mapexplorer.nearbyplaces.presentation.viewmodel.NearbyPlacesViewModel,
    onPlaceClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val mapState by mapViewModel.state.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    val basePlaces = mapState.nearbyPlacesInMap

    val filteredPlaces = remember(basePlaces, searchQuery) {
        if (searchQuery.isBlank()) basePlaces
        else basePlaces.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        // ── Header "← Volver al Mapa" ──────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = AppTheme.colors.textPrimary,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.navigationSelector_backToMap),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textPrimary,
                modifier = Modifier.clickable { onBack() }
            )
        }

        // ── Barra de búsqueda ──────────────────────────────────────────────
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = "Buscar lugar...",
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textSecondary
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = AppTheme.colors.textSecondary
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor    = AppTheme.colors.primary,
                unfocusedBorderColor  = AppTheme.colors.border,
                focusedContainerColor = AppTheme.colors.surface,
                unfocusedContainerColor = AppTheme.colors.surface
            ),
            textStyle = AppTheme.typography.bodyMedium.copy(
                color = AppTheme.colors.textPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Título de sección ──────────────────────────────────────────────
        Text(
            text = stringResource(Res.string.navigationSelector_seeNearbyPlaces),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.textSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // ── Contenido ─────────────────────────────────────────────────────
        if (basePlaces.isEmpty()) {
            NoPlacesFoundContent()
        } else if (filteredPlaces.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay resultados para \"$searchQuery\"",
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textSecondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredPlaces, key = { it.id }) { place ->
                    PlaceListItem(
                        place   = place,
                        onClick = { onPlaceClick(place.id) }
                    )
                    HorizontalDivider(
                        modifier  = Modifier.padding(horizontal = 16.dp),
                        color     = AppTheme.colors.border.copy(alpha = 0.3f),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

// ── PlaceListItem — fiel al Figma ─────────────────────────────────────────────
@Composable
fun PlaceListItem(
    place: PlaceModel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícono circular de categoría
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(AppTheme.colors.primary.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text     = place.categoryIcon,
                fontSize = 22.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Textos: Tipo, Nombre, Descripción
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = place.category,
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textSecondary
            )
            Text(
                text       = place.name,
                style      = AppTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color      = AppTheme.colors.textPrimary,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            Text(
                text     = place.description.ifBlank { place.category },
                style    = AppTheme.typography.bodySmall,
                color    = AppTheme.colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Estrellas + flecha derecha
        Column(horizontalAlignment = Alignment.End) {
            StarRatingRow(rating = place.rating, starSize = 14.dp)
        }

        Spacer(modifier = Modifier.width(4.dp))

        Icon(
            imageVector     = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint            = AppTheme.colors.textSecondary,
            modifier        = Modifier.size(20.dp)
        )
    }
}

// ── Estrellas reutilizables ────────────────────────────────────────────────────
@Composable
fun StarRatingRow(
    rating: Float,
    starSize: androidx.compose.ui.unit.Dp = 16.dp
) {
    Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
        val filled = rating.toInt().coerceIn(0, 5)
        repeat(filled) {
            Icon(
                imageVector     = Icons.Default.Star,
                contentDescription = null,
                tint            = Color(0xFFFFC107),
                modifier        = Modifier.size(starSize)
            )
        }
        repeat(5 - filled) {
            Icon(
                imageVector     = Icons.Default.Star,
                contentDescription = null,
                tint            = Color(0xFFE0E0E0),
                modifier        = Modifier.size(starSize)
            )
        }
    }
}
// ── Estado vacío ──────────────────────────────────────────────────────────────
@Composable
private fun NoPlacesFoundContent() {
    Box(
        modifier          = Modifier.fillMaxSize(),
        contentAlignment  = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "🗺️", fontSize = 56.sp)
            Text(
                text  = "Sigue explorando el mapa",
                style = AppTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.textPrimary
            )
            Text(
                text  = "Los lugares aparecen cuando\ndescubres nuevas zonas",
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textSecondary
            )
        }
    }
}