package com.ucb.mapexplorer.dollar.data.mapper

import com.ucb.mapexplorer.dollar.data.entity.DollarEntity
import com.ucb.mapexplorer.dollar.domain.model.DollarModel

fun DollarModel.toEntity() = DollarEntity(
    dollarOfficial,
    dollarParallel
)

fun DollarEntity.toModel() =  DollarModel(
    id,
    dollarOfficial,
    dollarParallel
)