package com.ucb.mapexplorer.map.domain.model

data class PlaceModel(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val descripcion:String,
    val category: String
)