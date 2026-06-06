package com.ucb.mapexplorer.nearbyplaces.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.map.presentation.viewmodel.MapViewModel
import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel
import com.ucb.mapexplorer.nearbyplaces.presentation.state.NearbyPlacesEvent
import com.ucb.mapexplorer.nearbyplaces.presentation.viewmodel.NearbyPlacesViewModel
import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailScreen(
    placeId: String,
    mapViewModel: MapViewModel,
    nearbyViewModel: NearbyPlacesViewModel,
    onBack: () -> Unit
) {
    val mapState    by mapViewModel.state.collectAsStateWithLifecycle()
    val detailState by nearbyViewModel.state.collectAsStateWithLifecycle()

    // 1. Busca en la lista local del mapa (sin red)
    val lugarLocal = remember(mapState.nearbyPlacesInMap, placeId) {
        mapState.nearbyPlacesInMap.find { it.id == placeId }
    }

    // 2. Si no está en memoria, carga desde Room/API
    LaunchedEffect(placeId) {
        if (lugarLocal == null) {
            nearbyViewModel.onEvent(NearbyPlacesEvent.OnSelectPlace(placeId))
        }
    }

    val lugar = lugarLocal ?: detailState.selectedPlace

    // ── Layout raíz ───────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        // ── TopBar "← Ver lugares cercanos a mi" (usa tu barra existente) ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint               = AppTheme.colors.textPrimary,
                modifier           = Modifier
                    .size(22.dp)
                    .clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text     = stringResource(Res.string.navigationSelector_seeNearbyPlaces),
                style    = AppTheme.typography.bodyMedium,
                color    = AppTheme.colors.textPrimary,
                modifier = Modifier.clickable { onBack() }
            )
        }

        // ── Contenido ─────────────────────────────────────────────────────
        when {
            lugar == null && detailState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppTheme.colors.primary)
                }
            }

            lugar == null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text  = detailState.errorMessage ?: "Lugar no disponible",
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.textSecondary
                    )
                }
            }

            else -> PlaceDetailContent(
                lugar      = lugar,
                onBack     = onBack,
                onViewMap  = {
                    mapViewModel.centerMapOnLocation(lugar.latitude, lugar.longitude)
                    onBack()
                }
            )
        }
    }
}

// ── Contenido principal del detalle ───────────────────────────────────────────
@Composable
private fun PlaceDetailContent(
    lugar: PlaceModel,
    onBack: () -> Unit,
    onViewMap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── 1. Imagen del lugar ────────────────────────────────────────────
        // Intentamos con Wikimedia Commons / Mapillary si la URL viene del mapper.
        // Si no hay URL, mostramos un placeholder con emoji grande + nombre.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(AppTheme.colors.surface),
            contentAlignment = Alignment.Center
        ) {
            if (!lugar.imageUrl.isNullOrBlank()) {
                // ✅ Sin crossfade — compatible con tu versión de Coil
                AsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(lugar.imageUrl)
                        .build(),
                    contentDescription = lugar.name,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize()
                )
            } else {
                // Placeholder: fondo gris suave + emoji grande de la categoría
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppTheme.colors.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = lugar.categoryIcon, fontSize = 72.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text  = lugar.name,
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colors.textSecondary
                        )
                    }
                }
            }
        }

        // ── 2. Título + botones favorito/guardar ──────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text       = lugar.name,
                style      = AppTheme.typography.headlineLarge.copy(fontSize = 22.sp),
                color      = AppTheme.colors.textPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier   = Modifier.weight(1f)
            )
            IconButton(onClick = { /* TODO: favorito */ }) {
                Icon(Icons.Default.FavoriteBorder, "Favorito", tint = AppTheme.colors.textPrimary)
            }
            IconButton(onClick = { /* TODO: guardar */ }) {
                Icon(Icons.Outlined.BookmarkBorder, "Guardar", tint = AppTheme.colors.textPrimary)
            }
        }

        // ── 3. Categoría + ícono + distancia ─────────────────────────────
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text  = lugar.category,
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textSecondary
            )
            Text(text = "·", color = AppTheme.colors.textSecondary)
            Text(text = lugar.categoryIcon, fontSize = 16.sp)

            if (lugar.distanceMeters > 0) {
                Text(text = "·", color = AppTheme.colors.textSecondary)
                val km = (lugar.distanceMeters / 1000)
                val kmInt = km.toInt()
                val kmDec = ((km - kmInt) * 10).toInt()
                val distText = if (lugar.distanceMeters >= 1000)
                    "$kmInt.$kmDec km"
                else
                    "${lugar.distanceMeters.toInt()} m"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint               = AppTheme.colors.primary,
                        modifier           = Modifier.size(14.dp)
                    )
                    Text(
                        text  = distText,
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── 4. Descripción general ────────────────────────────────────────
        // Muestra la descripción enriquecida (horario, teléfono, dirección, etc.)
        // que viene del mapper. Si está vacía muestra un placeholder.
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text       = stringResource(Res.string.placeDetails_description),
                style      = AppTheme.typography.labelLarge,
                color      = Color(0xFF00796B),   // verde teal del Figma
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (lugar.description.isNotBlank()) {
                // Las partes de la descripción están separadas por " · "
                // Renderizamos cada parte en su propio Text para mejor legibilidad
                val parts = lugar.description.split(" · ")
                parts.forEach { part ->
                    Text(
                        text   = part,
                        style  = AppTheme.typography.bodyMedium,
                        color  = AppTheme.colors.textSecondary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            } else {
                Text(
                    text  = "Información no disponible para este lugar.",
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textSecondary.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── 5. Estrellas de calificación ──────────────────────────────────
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val filled = lugar.rating.toInt().coerceIn(0, 5)
            repeat(filled) {
                Icon(
                    imageVector        = Icons.Default.Star,
                    contentDescription = null,
                    tint               = Color(0xFFFFC107),
                    modifier           = Modifier.size(36.dp)
                )
            }
            repeat(5 - filled) {
                Icon(
                    imageVector        = Icons.Default.Star,
                    contentDescription = null,
                    tint               = Color(0xFFE0E0E0),
                    modifier           = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // ── 6. Acciones tipo link (Ver en el mapa, Guíame, Compartir) ─────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text     = stringResource(Res.string.placeDetails_seeOnMap),
                fontSize = 22.sp,
                color    = Color(0xFF4285F4),
                modifier = Modifier.clickable { onViewMap() }
            )
            Text(
                text     = stringResource(Res.string.placeDetails_guideMe),
                fontSize = 22.sp,
                color    = Color(0xFF4285F4),
                modifier = Modifier.clickable { /* TODO: abrir navegación */ }
            )
            Text(
                text     = stringResource(Res.string.placeDetails_share),
                fontSize = 22.sp,
                color    = Color(0xFF4285F4),
                modifier = Modifier.clickable { /* TODO: compartir */ }
            )
        }
    }
}