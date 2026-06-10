package com.ucb.mapexplorer.map.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ucb.mapexplorer.map.presentation.state.MapUIState

@Composable
expect fun MapViewContainer(
    modifier: Modifier = Modifier,
    state: MapUIState,
    navController: NavController,
    onLocationChanged: (Double, Double) -> Unit
)
