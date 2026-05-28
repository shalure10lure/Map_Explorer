package com.ucb.mapexplorer.onboarding.data.datasource

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.tasks.await

actual class RemoteConfigDataSource actual constructor() {

    private val remoteConfig = Firebase.remoteConfig

    actual suspend fun fetchOnboardingJson(): String {
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings { minimumFetchIntervalInSeconds = 0 }
        ).await()
        remoteConfig.setDefaultsAsync(
            mapOf("onboarding_config" to "[]")
        ).await()
        remoteConfig.fetchAndActivate().await()
        return remoteConfig.getString("onboarding_config")
    }
}