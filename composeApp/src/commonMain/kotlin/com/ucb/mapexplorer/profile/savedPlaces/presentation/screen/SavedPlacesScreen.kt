package com.ucb.mapexplorer.profile.savedPlaces.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.mapexplorer.profile.savedPlaces.presentation.state.SavedPlacesEffect
import com.ucb.mapexplorer.profile.savedPlaces.presentation.state.SavedPlacesEvent
import com.ucb.mapexplorer.profile.savedPlaces.presentation.viewmodel.SavedPlacesViewModel

@Composable
fun SavedPlacesScreen(
    viewModel: SavedPlacesViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SavedPlacesEffect.NavigateBack -> onBack()
                is SavedPlacesEffect.NavigateToPlaceDetail -> {
                    // Navegar al detalle del sitio
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { viewModel.onEvent(SavedPlacesEvent.OnBackClick) }) {
                Text(text = "← Volver al Mapa", fontSize = 16.sp, color = Color.Black)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            items(state.places) { place ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .clickable { viewModel.onEvent(SavedPlacesEvent.OnPlaceClick(place)) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon Placeholder (Bookmark)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFF5F5F5), shape = androidx.compose.foundation.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔖")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = place.name, fontWeight = FontWeight.Bold)
                        Text(text = place.description, fontSize = 12.sp, color = Color.Gray)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🔖", color = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "⭐".repeat(place.rating.toInt()), color = Color(0xFFFFD700))
                    }
                }
                HorizontalDivider(color = Color(0xFFEEEEEE))
            }
        }
    }
}
