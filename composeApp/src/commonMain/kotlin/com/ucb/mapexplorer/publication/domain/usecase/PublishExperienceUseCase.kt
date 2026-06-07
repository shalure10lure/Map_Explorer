package com.ucb.mapexplorer.publication.domain.usecase

import com.ucb.mapexplorer.publication.domain.model.PublicationModel
import com.ucb.mapexplorer.publication.domain.repository.PublicationRepository

class PublishExperienceUseCase(private val repository: PublicationRepository) {
    suspend operator fun invoke(publication: PublicationModel): Boolean =
        repository.publishExperience(publication)
}