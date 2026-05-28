package com.ucb.mapexplorer.onboarding.data.repository

import com.ucb.mapexplorer.onboarding.data.datasource.OnboardingPreferences
import com.ucb.mapexplorer.onboarding.data.datasource.RemoteConfigDataSource
import com.ucb.mapexplorer.onboarding.domain.model.OnboardingPageModel
import com.ucb.mapexplorer.onboarding.domain.repository.OnboardingRepository
import kotlinx.serialization.json.*

class OnboardingRepositoryImpl(
    private val remote: RemoteConfigDataSource,
    private val prefs: OnboardingPreferences
) : OnboardingRepository {

    override suspend fun getPages(languageCode: String): List<OnboardingPageModel> {
        val raw = remote.fetchOnboardingJson()
        // El JSON tiene la key "onboarding_config" como array
        val root = Json.parseToJsonElement(raw)
        val array = when {
            root is JsonObject -> root["onboarding_config"]?.jsonArray ?: JsonArray(emptyList())
            root is JsonArray  -> root
            else               -> JsonArray(emptyList())
        }
        val lang = languageCode.take(2)

        return array.map { el ->
            val obj = el.jsonObject
            fun localized(key: String) =
                obj[key]!!.jsonObject[lang]?.jsonPrimitive?.content
                    ?: obj[key]!!.jsonObject["en"]!!.jsonPrimitive.content

            OnboardingPageModel(
                id          = obj["id"]!!.jsonPrimitive.int,
                title       = localized("title"),
                description = localized("description"),
                imageUrl    = localized("image_url")
            )
        }
    }

    override suspend fun isOnboardingCompleted() = prefs.isCompleted()
    override suspend fun markOnboardingCompleted() = prefs.setCompleted()
}