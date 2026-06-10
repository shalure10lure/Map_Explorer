package com.ucb.mapexplorer.map.presentation.screen

import androidx.compose.runtime.Composable

@Composable
expect fun  GuideMapScreen(
    userLat: Double,
    userLon: Double,
    destLat: Double,
    destLon: Double,
    placeName: String,
    onBack: () -> Unit
)