package com.ucb.mapexplorer.onboarding.domain.repository


import com.ucb.mapexplorer.onboarding.domain.model.OnboardingPageModel

interface OnboardingRepository {
    suspend fun getPages(languageCode: String): List<OnboardingPageModel>
    suspend fun isOnboardingCompleted(): Boolean
    suspend fun markOnboardingCompleted()
}