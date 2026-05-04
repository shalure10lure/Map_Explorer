package com.ucb.mapexplorer.dollar.presentation.state

import com.ucb.mapexplorer.dollar.domain.model.DollarModel

data class DollarUIState(
    val list: List<DollarModel> = emptyList(),
    val isLoading: Boolean = true
)
