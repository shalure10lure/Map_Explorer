package com.ucb.mapexplorer.map.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.map.domain.model.PlaceModel
import com.ucb.mapexplorer.map.presentation.state.MapEffect
import com.ucb.mapexplorer.map.presentation.state.MapEvent
import com.ucb.mapexplorer.map.presentation.viewmodel.MapViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = koinViewModel(parameters = { parametersOf(uid) })
) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MapEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                is MapEffect.ShowError -> snackbarHostState.showSnackbar("Error: ${effect.message}")
                MapEffect.CenterMapOnUser -> { }
            }
        }
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        sheetContent = {
            MapSettingsContent()
        },
        sheetPeekHeight = 80.dp,
        sheetContainerColor = AppTheme.colors.surface,
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).background(Color.Black)) {
            MapViewContainer(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onLocationChanged = { lat, lon ->
                    viewModel.onEvent(MapEvent.OnLocationUpdated(lat, lon))
                }
            )

            LevelBadge(state.level, state.experience)

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            state.errorMessage?.let { error ->
                AlertDialog(
                    onDismissRequest = { viewModel.onEvent(MapEvent.OnDismissError) },
                    confirmButton = {
                        TextButton(onClick = { viewModel.onEvent(MapEvent.OnDismissError) }) {
                            Text("OK")
                        }
                    },
                    title = { Text("Aviso") },
                    text = { Text(error) }
                )
            }
        }

    }
}

@Composable
fun LevelBadge(level: Int, experience: Int) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text("Nivel $level", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        val progress = (experience % 100) / 100f
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.width(100.dp).height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = Color.Cyan,
            trackColor = Color.Gray
        )
    }
}

@Composable
fun PlaceDetailContent(place: PlaceModel) {
    Column(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 32.dp)) {
        AsyncImage(
            model = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4",
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = place.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(text = "📍 Lugar Descubierto", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("❤️", modifier = Modifier.clickable { }.padding(8.dp), fontSize = 24.sp)
                Text("🔖", modifier = Modifier.clickable { }.padding(8.dp), fontSize = 24.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6495ED)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Ver en el mapa", color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F0FE)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Guíame al lugar", color = Color(0xFF6495ED))
        }
    }
}

@Composable
private fun MapSettingsContent() {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("⚙️ Configuración", fontWeight = FontWeight.Bold)
    }
}
