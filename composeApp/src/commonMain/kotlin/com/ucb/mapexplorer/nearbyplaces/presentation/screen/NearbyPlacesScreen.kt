package com.ucb.mapexplorer.nearbyplaces.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel
import com.ucb.mapexplorer.nearbyplaces.presentation.components.ExploreTab
import com.ucb.mapexplorer.nearbyplaces.presentation.components.ExploreTopBar
import com.ucb.mapexplorer.nearbyplaces.presentation.state.NearbyPlacesEvent
import com.ucb.mapexplorer.nearbyplaces.presentation.viewmodel.NearbyPlacesViewModel
import kotlin.math.roundToInt
import org.koin.compose.viewmodel.koinViewModel

private val AppRed   = Color(0xFFCC0000)
private val StarGold = Color(0xFFFFB300)
private val TextGray = Color(0xFF666666)
private val ChipBg   = Color(0xFFF5F5F5)

@Composable
fun NearbyPlacesScreen(
    userLat: Double,
    userLon: Double,
    onNavigateBack: () -> Unit,
    onPlaceSelected: (String) -> Unit,
    viewModel: NearbyPlacesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(userLat, userLon) {
        viewModel.onEvent(NearbyPlacesEvent.OnLoadPlaces(userLat, userLon))
    }

    Scaffold(
        topBar = {
            Column {
                ExploreTopBar(
                    activeTab     = ExploreTab.NEARBY,
                    onMapClick    = onNavigateBack,
                    onNearbyClick = { /* ya estamos aquí */ }
                )
                NearbyPlacesTopBar(
                    searchQuery    = state.searchQuery,
                    onQueryChanged = { viewModel.onEvent(NearbyPlacesEvent.OnSearchQueryChanged(it)) },
                    onBack         = onNavigateBack
                )
            }
        },
        containerColor = Color.White
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            if (state.categories.isNotEmpty()) {
                CategoryChips(
                    categories       = state.categories,
                    selectedCategory = state.selectedCategory,
                    onSelect         = { viewModel.onEvent(NearbyPlacesEvent.OnCategorySelected(it)) }
                )
            }

            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AppRed)
                    }
                }

                state.filteredPlaces.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No se encontraron lugares cercanos", color = TextGray)
                    }
                }

                else -> {
                    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                        items(state.filteredPlaces, key = { it.id }) { place ->
                            PlaceListItem(
                                place   = place,
                                onClick = {
                                    viewModel.onEvent(NearbyPlacesEvent.OnSelectPlace(place.id))
                                    onPlaceSelected(place.id)
                                }
                            )
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color     = Color(0xFFEEEEEE),
                                modifier  = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }

            state.errorMessage?.let { msg ->
                Snackbar(
                    action = {
                        TextButton(onClick = { viewModel.onEvent(NearbyPlacesEvent.OnDismissError) }) {
                            Text("OK", color = AppRed)
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) { Text(msg) }
            }
        }
    }
}

@Composable
private fun NearbyPlacesTopBar(
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("← Volver al Mapa", fontSize = 16.sp, color = Color.Black)
            }
        }

        OutlinedTextField(
            value         = searchQuery,
            onValueChange = onQueryChanged,
            placeholder   = { Text("🔍 Buscar lugar...", color = TextGray) },
            singleLine    = true,
            shape         = RoundedCornerShape(12.dp),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = AppRed,
                unfocusedBorderColor = Color(0xFFDDDDDD)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun CategoryChips(
    categories: List<String>,
    selectedCategory: String?,
    onSelect: (String?) -> Unit
) {
    LazyRow(
        contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick  = { onSelect(null) },
                label    = { Text("Todos") },
                colors   = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppRed,
                    selectedLabelColor     = Color.White
                )
            )
        }
        items(categories) { cat ->
            FilterChip(
                selected = selectedCategory == cat,
                onClick  = { onSelect(if (selectedCategory == cat) null else cat) },
                label    = { Text(cat) },
                colors   = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppRed,
                    selectedLabelColor     = Color.White
                )
            )
        }
    }
}

@Composable
private fun PlaceListItem(
    place: PlaceModel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ChipBg),
            contentAlignment = Alignment.Center
        ) {
            Text(place.categoryIcon, fontSize = 22.sp)
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = place.category,
                fontSize   = 11.sp,
                color      = TextGray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text       = place.name,
                fontSize   = 15.sp,
                fontWeight = FontWeight.Bold,
                color      = Color.Black
            )
            if (place.description.isNotBlank()) {
                Text(
                    text     = place.description,
                    fontSize = 12.sp,
                    color    = TextGray,
                    maxLines = 1
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.End) {
            StarRatingRow(rating = place.rating)
            Spacer(Modifier.height(2.dp))
            Text(
                text     = formatDistance(place.distanceMeters),
                fontSize = 11.sp,
                color    = TextGray
            )
        }

        Spacer(Modifier.width(4.dp))
        Text("›", fontSize = 22.sp, color = Color(0xFFCCCCCC))
    }
}

@Composable
fun StarRatingRow(rating: Float, maxStars: Int = 5) {
    Row {
        repeat(maxStars) { index ->
            val filled = index < rating.roundToInt()
            Text(
                text     = if (filled) "★" else "☆",
                fontSize = 14.sp,
                color    = if (filled) StarGold else Color(0xFFDDDDDD)
            )
        }
    }
}

private fun formatDistance(meters: Double): String = when {
    meters <= 0   -> ""
    meters < 1000 -> "${meters.toInt()} m"
    else          -> "${(meters / 1000 * 10).roundToInt() / 10.0} km"
}