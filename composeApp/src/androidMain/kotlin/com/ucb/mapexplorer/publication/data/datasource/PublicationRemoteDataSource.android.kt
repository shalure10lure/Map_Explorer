package com.ucb.mapexplorer.publication.data.datasource

import com.google.firebase.database.FirebaseDatabase
import com.ucb.mapexplorer.publication.domain.model.PublicationModel
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock

actual class PublicationRemoteDataSource actual constructor() {

    private val db = FirebaseDatabase.getInstance().reference

    actual suspend fun publish(publication: PublicationModel): Boolean {
        return try {
            val pubId = db.child("publicaciones").push().key ?: return false
            db.child("publicaciones").child(pubId).setValue(
                mapOf(
                    "uid"          to publication.uid,
                    "userName"     to publication.userName,
                    "lugarId"      to publication.lugarId,
                    "locationName" to publication.locationName,
                    "category"     to publication.category,
                    "categoryIcon" to publication.categoryIcon,
                    "imageUrl"     to (publication.imageUrl ?: ""),
                    "rating"       to publication.rating,
                    "experience"   to publication.experience,
                    "publishedAt"  to publication.publishedAt,
                    "latitude"     to publication.latitude,
                    "longitude"    to publication.longitude
                )
            ).await()

            // También guardar el rating en el nodo del lugar para calcular promedio
            val safeId = publication.lugarId.replace(Regex("[.#\$\\[\\]/]"), "_")
            db.child("ratings").child(safeId).child(publication.uid)
                .setValue(publication.rating).await()

            true
        } catch (e: Exception) {
            println("❌ Error publicando: ${e.message}")
            false
        }
    }

    actual suspend fun getAllPublications(): List<PublicationModel> {
        return try {
            val snapshot = db.child("publicaciones").get().await()
            snapshot.children.mapNotNull { child ->
                try {
                    PublicationModel(
                        id           = child.key ?: return@mapNotNull null,
                        uid          = child.child("uid").getValue(String::class.java) ?: "",
                        userName     = child.child("userName").getValue(String::class.java) ?: "",
                        lugarId      = child.child("lugarId").getValue(String::class.java) ?: "",
                        locationName = child.child("locationName").getValue(String::class.java) ?: "",
                        category     = child.child("category").getValue(String::class.java) ?: "",
                        categoryIcon = child.child("categoryIcon").getValue(String::class.java) ?: "📍",
                        imageUrl     = child.child("imageUrl").getValue(String::class.java)?.takeIf { it.isNotBlank() },
                        rating       = child.child("rating").getValue(Int::class.java) ?: 0,
                        experience   = child.child("experience").getValue(String::class.java) ?: "",
                        publishedAt  = child.child("publishedAt").getValue(Long::class.java) ?: 0L,
                        latitude     = child.child("latitude").getValue(Double::class.java) ?: 0.0,
                        longitude    = child.child("longitude").getValue(Double::class.java) ?: 0.0
                    )
                } catch (e: Exception) { null }
            }.sortedByDescending { it.publishedAt }
        } catch (e: Exception) {
            println("❌ Error obteniendo publicaciones: ${e.message}")
            emptyList()
        }
    }

    actual suspend fun getPlaceAverageRating(lugarId: String): Float {
        return try {
            val safeId = lugarId.replace(Regex("[.#\$\\[\\]/]"), "_")
            val snapshot = db.child("ratings").child(safeId).get().await()
            val ratings = snapshot.children.mapNotNull {
                it.getValue(Int::class.java)?.toFloat()
            }
            if (ratings.isEmpty()) 0f else ratings.average().toFloat()
        } catch (e: Exception) { 0f }
    }
}