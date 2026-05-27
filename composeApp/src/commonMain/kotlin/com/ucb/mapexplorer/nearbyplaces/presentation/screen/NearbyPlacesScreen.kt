package com.ucb.mapexplorer.nearbyplaces.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.ucb.mapexplorer.nearbyplaces.presentation.state.NearbyPlacesEvent
import com.ucb.mapexplorer.nearbyplaces.presentation.viewmodel.NearbyPlacesViewModel

@Composable
fun NearbyPlacesScreen(
    viewModel: NearbyPlacesViewModel,
    onPlaceClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column {
        // 🔍 Barra de búsqueda
        TextField(
            value = state.searchQuery,
            onValueChange = { viewModel.onEvent(NearbyPlacesEvent.OnSearchQueryChanged(it)) },
            placeholder = { Text("Buscar lugares...") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        // 📂 Filtro por categoría
        LazyRow(modifier = Modifier.padding(8.dp)) {
            items(state.categories) { category ->
                Button(
                    onClick = { viewModel.onEvent(NearbyPlacesEvent.OnCategorySelected(category)) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.selectedCategory == category) Color.Gray else Color.LightGray
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(category)
                }
            }
        }

        // 📍 Lista de lugares
        LazyColumn {
            items(state.filteredPlaces) { place ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onPlaceClick(place.id) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        // Imagen del lugar (si existe)
                        place.imageUrl?.let {
                            AsyncImage(
                                model = it,
                                contentDescription = place.name,
                                modifier = Modifier.size(64.dp)
                            )
                        } ?: Text(place.categoryIcon, fontSize = 32.sp)

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(place.name, style = MaterialTheme.typography.titleMedium)
                            Text("${place.distanceMeters.toInt()} m", style = MaterialTheme.typography.bodySmall)
                            Text(place.category, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
