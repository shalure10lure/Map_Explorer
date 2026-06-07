package com.ucb.mapexplorer.nearbyplaces.domain.usecase

import com.ucb.mapexplorer.nearbyplaces.domain.repository.FavoritosRepository

class ToggleFavoritoUseCase(private val repository: FavoritosRepository) {
    suspend operator fun invoke(
        uid: String, lugarId: String, nombre: String,
        categoria: String, lat: Double, lon: Double, iconoCategoria: String
    ): Boolean = repository.toggleFavorito(uid, lugarId, nombre, categoria, lat, lon, iconoCategoria)
}