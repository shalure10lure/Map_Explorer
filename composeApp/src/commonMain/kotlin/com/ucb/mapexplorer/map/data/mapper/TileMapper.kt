package com.ucb.mapexplorer.map.data.mapper

import com.ucb.mapexplorer.map.data.entity.TileEntity
import com.ucb.mapexplorer.map.domain.model.TileModel

fun TileEntity.toModel(): TileModel {
    return TileModel(
        tileX = tileX,
        tileY = tileY,
        discovered = true,
        discoveredAt = descubiertoEn,
        visitCount = vecesVisitado,
        explorationPercentage = porcentajeExplorado,
        lastVisited = ultimoIngreso
    )
}