package com.ucb.mapexplorer.nearbyplaces.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.map.presentation.viewmodel.MapViewModel
import com.ucb.mapexplorer.nearbyplaces.presentation.state.NearbyPlacesEvent
import com.ucb.mapexplorer.nearbyplaces.presentation.viewmodel.NearbyPlacesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailScreen(
    placeId: String,
    mapViewModel: MapViewModel,           // 🗺️ Carga inicial si viene desde el pin del Mapa
    nearbyViewModel: NearbyPlacesViewModel, // 🔌 Respaldo de red/API si viene desde NearbyPlaces
    onBack: () -> Unit
) {
    val mapState by mapViewModel.state.collectAsStateWithLifecycle()
    val detailState by nearbyViewModel.state.collectAsStateWithLifecycle()

    // 1. Intentamos recuperar el lugar desde la lista en memoria del mapa
    val lugarLocal = remember(mapState.nearbyPlacesInMap, placeId) {
        mapState.nearbyPlacesInMap.find { it.id == placeId }
    }

    // 2. Si no se halla localmente, disparamos la petición a la API
    LaunchedEffect(lugarLocal, placeId) {
        if (lugarLocal == null) {
            nearbyViewModel.onEvent(NearbyPlacesEvent.OnSelectPlace(placeId))
        }
    }

    // 3. Lugar definitivo para renderizar
    val lugarDefinitivo = lugarLocal ?: detailState.selectedPlace

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ver lugares cercanos a mi",
                        style = AppTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                        color = AppTheme.colors.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = AppTheme.colors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppTheme.colors.surface)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppTheme.colors.background)
        ) {
            if (lugarLocal == null && detailState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AppTheme.colors.primary
                )
            } else if (lugarDefinitivo != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // --- 1. Imagen del Sitio (Fiel a la foto) ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .background(Color.LightGray), // Reemplazar por AsyncImage/Koil si usas URL de red
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (!lugarDefinitivo.imageUrl.isNullOrBlank()) "📸 [Imagen de Red]" else "🏟️ [Estadio Félix Capriles]",
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // --- 2. Título del Sitio y Botones Favorito/Guardar ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = lugarDefinitivo.name.ifBlank { "Estadio Félix Capriles." },
                            style = AppTheme.typography.labelLarge.copy(fontSize = 22.sp),
                            modifier = Modifier.weight(1f),
                            color = AppTheme.colors.textPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.FavoriteBorder, "Me gusta", tint = AppTheme.colors.textPrimary)
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Outlined.BookmarkBorder, "Guardar", tint = AppTheme.colors.textPrimary)
                        }
                    }

                    // --- 3. Categoría e Indicador de Accesibilidad (♿ / Icono) ---
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = lugarDefinitivo.category,
                            style = AppTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = AppTheme.colors.textSecondary
                        )
                        Text(text = "•", color = AppTheme.colors.textSecondary)
                        Text(
                            text = lugarDefinitivo.categoryIcon.ifBlank { "♿" },
                            style = AppTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- 4. Sección de Descripción General ---
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Text(
                            text = "Descripción general",
                            style = AppTheme.typography.labelLarge.copy(fontSize = 14.sp),
                            color = Color(0xFF00796B), // Color verde teal del diseño original
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = lugarDefinitivo.description.ifBlank { "Descripcion del lugar" },
                            style = AppTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                            color = AppTheme.colors.textSecondary.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    // --- 5. Estrellas de Calificación Grandes (Ubicadas exactamente como la imagen) ---
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val starsMax = 5
                        val filledLargeStars = lugarDefinitivo.rating.toInt().coerceIn(0, starsMax)
                        repeat(filledLargeStars) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        repeat(starsMax - filledLargeStars) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = AppTheme.colors.border.copy(alpha = 0.4f),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    // --- 6. Enlaces Inferiores de Texto Azul Estilo "Link" ---
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 48.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text(
                            text = "Ver en el mapa",
                            fontSize = 22.sp,
                            color = Color(0xFF4285F4),
                            modifier = Modifier.clickable {
                                lugarDefinitivo?.let {
                                    // Centramos el mapa en las coordenadas del lugar
                                    mapViewModel.centerMapOnLocation(it.latitude, it.longitude)
                                    // Regresamos a la pantalla del mapa
                                    onBack()
                                }
                            }
                        )
                        Text(
                            text = "Guíame al lugar",
                            fontSize = 24.sp,
                            color = Color(0xFF4285F4),
                            modifier = Modifier.clickable { /* Acción de Navegación */ }
                        )
                        Text(
                            text = "Compartir mi experiencia",
                            fontSize = 24.sp,
                            color = Color(0xFF4285F4),
                            modifier = Modifier.clickable { /* Acción de Compartir */ }
                        )
                    }
                }
            } else {
                // Estado vacío / Errores
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = detailState.errorMessage ?: "Lugar no disponible en esta zona.",
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}