package com.ucb.mapexplorer.map.data.mapper

import com.ucb.mapexplorer.map.data.entity.TileEntity
import com.ucb.mapexplorer.map.domain.model.TileModel

fun TileEntity.toModel(): TileModel = TileModel(
    tileX = tileX,
    tileY = tileY,
    discovered = true,
    discoveredAt = descubiertoEn,
    visitCount = vecesVisitado,
    lastVisited = ultimoIngreso
)

fun TileModel.toEntity(uid: String): TileEntity = TileEntity(
    uid = uid,
    tileX = tileX,
    tileY = tileY,
    descubiertoEn = discoveredAt,
    vecesVisitado = visitCount,
    ultimoIngreso = lastVisited,
    sincronizado = false
)
