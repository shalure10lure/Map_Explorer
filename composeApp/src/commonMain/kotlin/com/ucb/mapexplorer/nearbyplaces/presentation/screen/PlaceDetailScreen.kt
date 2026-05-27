package com.ucb.mapexplorer.nearbyplaces.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.ucb.mapexplorer.nearbyplaces.presentation.viewmodel.NearbyPlacesViewModel

@Composable
fun PlaceDetailScreen(
    viewModel: NearbyPlacesViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val place = state.selectedPlace ?: return

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Imagen principal
        place.imageUrl?.let {
            AsyncImage(
                model = it,
                contentDescription = place.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        } ?: Text(place.categoryIcon, fontSize = 64.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(place.name, style = MaterialTheme.typography.headlineSmall)
        Text(place.category, style = MaterialTheme.typography.bodyMedium)
        Text(place.description, style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Distancia: ${place.distanceMeters.toInt()} m", style = MaterialTheme.typography.bodyMedium)
        Text("Rating: ${place.rating}", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // Botones de acción
        Row {
            Button(onClick = { /* TODO: compartir */ }) {
                Text("Compartir")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { onBack() }) {
                Text("Volver")
            }
        }
    }
}
