package com.ucb.mapexplorer.nearbyplaces.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.map.presentation.viewmodel.MapViewModel
import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel
import com.ucb.mapexplorer.nearbyplaces.presentation.viewmodel.NearbyPlacesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyPlacesScreen(
    mapViewModel: MapViewModel, // 🚀 Consistencia total con el Mapa
    viewModel: NearbyPlacesViewModel,
    onPlaceClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val mapState by mapViewModel.state.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    // 🎯 REGLA DE ORO: Solo mostramos lo que el mapa ya filtró (Tiles abiertos)
    val basePlaces = mapState.nearbyPlacesInMap

    val filteredPlacesList = remember(basePlaces, searchQuery) {
        basePlaces.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lugares Cercanos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color.White)) {
            // Buscador estilizado
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar restaurante, museo...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(24.dp)
            )

            if (filteredPlacesList.isEmpty()) {
                NoPlacesFound()
            } else {
                LazyColumn {
                    items(filteredPlacesList) { place ->
                        PlaceItem(place = place, onClick = { onPlaceClick(place.id) })
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF5F5F5))
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceItem(place: PlaceModel, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono circular de la imagen
        Surface(
            shape = CircleShape,
            border = BorderStroke(1.dp, Color.LightGray),
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(place.categoryIcon, fontSize = 20.sp)
            }
        }

        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(place.category, style = AppTheme.typography.bodySmall, color = Color.Gray)
            Text(place.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(place.description, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.Gray)
        }

        StarRating(place.rating) // Estrellas a la derecha
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.LightGray)
    }
}

@Composable
fun StarRating(rating: Float) {
    Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
        val ratingMax = 5
        val filledStars = rating.toInt().coerceIn(0, ratingMax)
        repeat(filledStars) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
        }
        repeat(ratingMax - filledStars) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFE0E0E0), modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun NoPlacesFound() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🗺️", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "No hay lugares descubiertos aquí.", 
            style = AppTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            "¡Sigue explorando el mapa!", 
            style = AppTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}
