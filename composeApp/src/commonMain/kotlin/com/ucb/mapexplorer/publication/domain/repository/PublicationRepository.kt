package com.ucb.mapexplorer.publication.domain.repository

import com.ucb.mapexplorer.publication.domain.model.PublicationModel

interface PublicationRepository {
    suspend fun publishExperience(publication: PublicationModel): Boolean
    suspend fun getAllPublications(): List<PublicationModel>
    suspend fun getPlaceAverageRating(lugarId: String): Float
}