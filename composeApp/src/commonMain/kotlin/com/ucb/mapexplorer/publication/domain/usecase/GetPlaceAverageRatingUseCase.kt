package com.ucb.mapexplorer.publication.domain.usecase

import com.ucb.mapexplorer.publication.domain.repository.PublicationRepository

class GetPlaceAverageRatingUseCase(private val repository: PublicationRepository) {
    suspend operator fun invoke(lugarId: String): Float =
        repository.getPlaceAverageRating(lugarId)
}