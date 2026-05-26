package com.ucb.mapexplorer.map.presentation.state

sealed interface MapEffect {
    data class ShowError(val message: String) : MapEffect

    data class ShowMessage(
        val message: String
    ) : MapEffect
}