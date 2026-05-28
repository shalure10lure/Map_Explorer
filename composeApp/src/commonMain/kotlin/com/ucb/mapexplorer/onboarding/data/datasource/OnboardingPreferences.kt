package com.ucb.mapexplorer.onboarding.data.datasource

expect class OnboardingPreferences() {
    suspend fun isCompleted(): Boolean
    suspend fun setCompleted()
}