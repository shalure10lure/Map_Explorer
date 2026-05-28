package com.ucb.mapexplorer.onboarding.data.datasource

expect class RemoteConfigDataSource() {
    suspend fun fetchOnboardingJson(): String
}