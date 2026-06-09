package com.ucb.mapexplorer.onboarding.presentation.state

import com.ucb.mapexplorer.onboarding.domain.model.OnboardingPageModel

data class OnboardingUIState(
    val pages: List<OnboardingPageModel> = emptyList(),
    val currentIndex: Int = 0,
    val isLoading: Boolean = true,
    val navigateToHome: Boolean = false
)