package com.ucb.mapexplorer.publication.data.repository

import com.ucb.mapexplorer.publication.data.datasource.PublicationRemoteDataSource
import com.ucb.mapexplorer.publication.domain.model.PublicationModel
import com.ucb.mapexplorer.publication.domain.repository.PublicationRepository

class PublicationRepositoryImpl(
    private val remote: PublicationRemoteDataSource
) : PublicationRepository {

    override suspend fun publishExperience(publication: PublicationModel): Boolean =
        remote.publish(publication)

    override suspend fun getAllPublications(): List<PublicationModel> =
        remote.getAllPublications()

    override suspend fun getPlaceAverageRating(lugarId: String): Float =
        remote.getPlaceAverageRating(lugarId)
}