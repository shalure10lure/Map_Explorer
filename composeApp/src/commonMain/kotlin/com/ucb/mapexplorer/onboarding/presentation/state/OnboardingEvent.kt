package com.ucb.mapexplorer.onboarding.presentation.state


sealed class OnboardingEvent {
    object Next  : OnboardingEvent()
    object Back  : OnboardingEvent()
    object Skip  : OnboardingEvent()  // No persiste → reaparece al reiniciar
    object Start : OnboardingEvent()  // Persiste → nunca más aparece
}