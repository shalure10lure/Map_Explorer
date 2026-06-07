package com.ucb.mapexplorer.nearbyplaces.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Bookmark
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
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.core.session.Session
import com.ucb.mapexplorer.map.presentation.viewmodel.MapViewModel
import com.ucb.mapexplorer.navigation.NavRoute
import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.IsFavoritoUseCase
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.IsGuardadoUseCase
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.ToggleFavoritoUseCase
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.ToggleGuardadoUseCase
import com.ucb.mapexplorer.nearbyplaces.presentation.state.NearbyPlacesEvent
import com.ucb.mapexplorer.nearbyplaces.presentation.viewmodel.NearbyPlacesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import kotlin.math.round

@Composable
fun PlaceDetailScreen(
    placeId: String,
    mapViewModel: MapViewModel,
    nearbyViewModel: NearbyPlacesViewModel,
    navController: NavController,
    onBack: () -> Unit
) {
    val mapState    by mapViewModel.state.collectAsStateWithLifecycle()
    val detailState by nearbyViewModel.state.collectAsStateWithLifecycle()

    val lugarLocal = remember(mapState.nearbyPlacesInMap, placeId) {
        mapState.nearbyPlacesInMap.find { it.id == placeId }
    }

    LaunchedEffect(placeId) {
        if (lugarLocal == null) {
            nearbyViewModel.onEvent(NearbyPlacesEvent.OnSelectPlace(placeId))
        }
    }

    val lugar = lugarLocal ?: detailState.selectedPlace

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
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
                modifier           = Modifier.size(22.dp).clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text     = stringResource(Res.string.navigationSelector_seeNearbyPlaces),
                style    = AppTheme.typography.bodyMedium,
                color    = AppTheme.colors.textPrimary,
                modifier = Modifier.clickable { onBack() }
            )
        }

        when {
            lugar == null && detailState.isLoading ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppTheme.colors.primary)
                }
            lugar == null ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text  = detailState.errorMessage ?: "Lugar no disponible",
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.textSecondary
                    )
                }
            else -> PlaceDetailContent(
                lugar    = lugar,
                onBack   = onBack,
                onViewMap = {
                    mapViewModel.centerMapOnLocation(lugar.latitude, lugar.longitude)
                    onBack()
                },
                onGuideMe = {
                    val userLat = mapViewModel.state.value.userLat
                    val userLon = mapViewModel.state.value.userLng
                    navController.navigate(
                        NavRoute.GuideMap(
                            userLat   = userLat,
                            userLon   = userLon,
                            destLat   = lugar.latitude,
                            destLon   = lugar.longitude,
                            placeName = lugar.name
                        )
                    )
                },
                onShareExperience = {
                    navController.navigate(NavRoute.Publication(lugar.id))
                }
            )
        }
    }
}

@Composable
private fun PlaceDetailContent(
    lugar: PlaceModel,
    onBack: () -> Unit,
    onViewMap: () -> Unit,
    onGuideMe: () -> Unit,
    onShareExperience: () -> Unit
) {
    // Inyectamos use cases directamente — no necesitamos un ViewModel extra
    val toggleFavoritoUseCase: ToggleFavoritoUseCase = koinInject()
    val toggleGuardadoUseCase: ToggleGuardadoUseCase = koinInject()
    val isFavoritoUseCase: IsFavoritoUseCase         = koinInject()
    val isGuardadoUseCase: IsGuardadoUseCase         = koinInject()

    val scope  = rememberCoroutineScope()
    val uid    = Session.uid ?: ""

    var isFavorito by remember { mutableStateOf(false) }
    var isGuardado by remember { mutableStateOf(false) }

    // Cargar estado inicial
    LaunchedEffect(lugar.id) {
        if (uid.isNotBlank()) {
            isFavorito = isFavoritoUseCase(uid, lugar.id)
            isGuardado = isGuardadoUseCase(uid, lugar.id)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AppTheme.colors.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // ── 1. Imagen ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(AppTheme.colors.surface),
                contentAlignment = Alignment.Center
            ) {
                if (!lugar.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalPlatformContext.current)
                            .data(lugar.imageUrl).build(),
                        contentDescription = lugar.name,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize()
                    )
                } else {
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

            // ── 2. Título + botones Favorito/Guardar ───────────────────────
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

                // Botón Favorito
                IconButton(onClick = {
                    if (uid.isBlank()) return@IconButton
                    scope.launch(Dispatchers.IO) {
                        val resultado = toggleFavoritoUseCase(
                            uid, lugar.id, lugar.name, lugar.category,
                            lugar.latitude, lugar.longitude, lugar.categoryIcon
                        )
                        isFavorito = resultado
                        launch(Dispatchers.Main) {
                            snackbarHostState.showSnackbar(
                                if (resultado) "❤️ Añadido a favoritos"
                                else "Eliminado de favoritos"
                            )
                        }
                    }
                }) {
                    Icon(
                        imageVector = if (isFavorito) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorito) "Quitar favorito" else "Añadir a favoritos",
                        tint = if (isFavorito) AppTheme.colors.primary
                        else AppTheme.colors.textPrimary
                    )
                }

                // Botón Guardar
                IconButton(onClick = {
                    if (uid.isBlank()) return@IconButton
                    scope.launch(Dispatchers.IO) {
                        val resultado = toggleGuardadoUseCase(
                            uid, lugar.id, lugar.name, lugar.category,
                            lugar.latitude, lugar.longitude, lugar.categoryIcon
                        )
                        isGuardado = resultado
                        launch(Dispatchers.Main) {
                            snackbarHostState.showSnackbar(
                                if (resultado) "🔖 Guardado para más tarde"
                                else "Eliminado de guardados"
                            )
                        }
                    }
                }) {
                    Icon(
                        imageVector = if (isGuardado) Icons.Outlined.Bookmark
                        else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (isGuardado) "Quitar guardado" else "Guardar lugar",
                        tint = if (isGuardado) AppTheme.colors.primary
                        else AppTheme.colors.textPrimary
                    )
                }
            }

            // ── 3. Categoría + distancia ───────────────────────────────────
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

                    val distKm = round((lugar.distanceMeters / 1000) * 10) / 10

                    val distText = if (lugar.distanceMeters >= 1000) {
                        "$distKm km"
                    } else {
                        "${lugar.distanceMeters.toInt()} m"
                    }
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

            // ── 4. Descripción ─────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text       = stringResource(Res.string.placeDetails_description),
                    style      = AppTheme.typography.labelLarge,
                    color      = Color(0xFF00796B),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (lugar.description.isNotBlank()) {
                    lugar.description.split(" · ").forEach { part ->
                        Text(
                            text     = part,
                            style    = AppTheme.typography.bodyMedium,
                            color    = AppTheme.colors.textSecondary,
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

            // ── 5. Estrellas ───────────────────────────────────────────────
            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val filled = lugar.rating.toInt().coerceIn(0, 5)
                repeat(filled) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(36.dp))
                }
                repeat(5 - filled) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFE0E0E0), modifier = Modifier.size(36.dp))
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ── 6. Acciones ────────────────────────────────────────────────
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
                    modifier = Modifier.clickable { onGuideMe() }  // ← conectado
                )
                Text(
                    text     = stringResource(Res.string.placeDetails_share),
                    fontSize = 22.sp,
                    color    = Color(0xFF4285F4),
                    modifier = Modifier.clickable { onShareExperience() }  // ← conectado
                )
            }
        }
    }
}