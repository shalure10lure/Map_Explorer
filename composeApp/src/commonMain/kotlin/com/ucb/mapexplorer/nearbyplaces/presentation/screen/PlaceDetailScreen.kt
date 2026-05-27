package com.ucb.mapexplorer.nearbyplaces.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel
import com.ucb.mapexplorer.nearbyplaces.presentation.components.ExploreTab
import com.ucb.mapexplorer.nearbyplaces.presentation.components.ExploreTopBar
import com.ucb.mapexplorer.nearbyplaces.presentation.state.NearbyPlacesEvent
import com.ucb.mapexplorer.nearbyplaces.presentation.viewmodel.NearbyPlacesViewModel
import kotlin.math.roundToInt
import org.koin.compose.viewmodel.koinViewModel

private val AppRed   = Color(0xFFCC0000)
private val StarGold = Color(0xFFFFB300)
private val LinkBlue = Color(0xFF1565C0)

@Composable
fun PlaceDetailScreen(
    placeId: String,
    onNavigateBack: () -> Unit,
    onNavigateToMap: (Double, Double) -> Unit,
    viewModel: NearbyPlacesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(placeId) {
        viewModel.onEvent(NearbyPlacesEvent.OnSelectPlace(placeId))
    }

    val place = state.selectedPlace

    if (place == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AppRed)
        }
        return
    }

    PlaceDetailContent(
        place           = place,
        onBack          = {
            viewModel.onEvent(NearbyPlacesEvent.OnDismissDetail)
            onNavigateBack()
        },
        onNavigateToMap = onNavigateToMap
    )
}

@Composable
private fun PlaceDetailContent(
    place: PlaceModel,
    onBack: () -> Unit,
    onNavigateToMap: (Double, Double) -> Unit
) {
    var isFav   by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {

        // ── Panel rojo ────────────────────────────────────────────────────
        ExploreTopBar(
            activeTab     = ExploreTab.NEARBY,
            onMapClick    = onBack,
            onNearbyClick = onBack
        )

        // ── Imagen ────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            if (place.imageUrl != null) {
                AsyncImage(
                    model              = place.imageUrl,
                    contentDescription = place.name,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier         = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF0F0F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(place.categoryIcon, fontSize = 72.sp)
                }
            }

            TextButton(
                onClick  = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(50))
            ) {
                Text("← Ver lugares cercanos a mi", fontSize = 14.sp, color = Color.Black)
            }
        }

        // ── Contenido ─────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text       = place.name,
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.Black,
                    modifier   = Modifier.weight(1f)
                )
                TextButton(onClick = { isFav = !isFav }) {
                    Text(if (isFav) "❤️" else "🤍", fontSize = 20.sp)
                }
                TextButton(onClick = { isSaved = !isSaved }) {
                    Text(if (isSaved) "🔖" else "📌", fontSize = 20.sp)
                }
            }

            Spacer(Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(place.categoryIcon, fontSize = 14.sp)
                Spacer(Modifier.width(4.dp))
                Text(
                    text       = place.category,
                    fontSize   = 13.sp,
                    color      = AppRed,
                    fontWeight = FontWeight.Medium
                )
                if (place.distanceMeters > 0) {
                    Text(
                        text     = "  •  ${formatDistance(place.distanceMeters)}",
                        fontSize = 13.sp,
                        color    = Color(0xFF888888)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(Modifier.height(12.dp))

            Text(
                text       = "Descripción general",
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = AppRed
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text       = place.description.ifBlank { "Sin descripción disponible." },
                fontSize   = 14.sp,
                color      = Color(0xFF444444),
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                StarRatingRow(rating = place.rating.coerceAtLeast(3.5f))
                Spacer(Modifier.width(8.dp))
                if (place.rating > 0) {
                    Text(
                        text       = "${(place.rating * 10).roundToInt() / 10.0}",
                        fontSize   = 14.sp,
                        color      = StarGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            ActionLink("Ver en el mapa") { onNavigateToMap(place.latitude, place.longitude) }
            Spacer(Modifier.height(12.dp))
            ActionLink("Guíame al lugar") { onNavigateToMap(place.latitude, place.longitude) }
            Spacer(Modifier.height(12.dp))
            ActionLink("Compartir mi experiencia") { }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ActionLink(text: String, onClick: () -> Unit) {
    TextButton(
        onClick        = onClick,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text       = text,
            fontSize   = 15.sp,
            color      = LinkBlue,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatDistance(meters: Double): String = when {
    meters <= 0   -> ""
    meters < 1000 -> "${meters.toInt()} m"
    else          -> "${(meters / 1000 * 10).roundToInt() / 10.0} km"
}