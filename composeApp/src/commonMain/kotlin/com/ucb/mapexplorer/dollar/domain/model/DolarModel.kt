package com.ucb.mapexplorer.dollar.domain.model

data class DollarModel(
    var id: Int? = null,
    var dollarOfficial: String? = null,
    var dollarParallel: String? = null,
    var timestamp: Long = 0
)
