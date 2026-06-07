package com.ucb.mapexplorer.publication.data.datasource

import com.ucb.mapexplorer.publication.domain.model.PublicationModel

expect class PublicationRemoteDataSource() {
    suspend fun publish(publication: PublicationModel): Boolean
    suspend fun getAllPublications(): List<PublicationModel>
    suspend fun getPlaceAverageRating(lugarId: String): Float
}