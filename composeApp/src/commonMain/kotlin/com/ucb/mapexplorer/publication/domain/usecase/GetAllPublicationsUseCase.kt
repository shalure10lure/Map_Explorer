package com.ucb.mapexplorer.publication.domain.usecase

import com.ucb.mapexplorer.publication.domain.model.PublicationModel
import com.ucb.mapexplorer.publication.domain.repository.PublicationRepository

class GetAllPublicationsUseCase(private val repository: PublicationRepository) {
    suspend operator fun invoke(): List<PublicationModel> =
        repository.getAllPublications()
}