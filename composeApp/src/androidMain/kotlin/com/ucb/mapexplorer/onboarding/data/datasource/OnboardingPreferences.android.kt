package com.ucb.mapexplorer.onboarding.data.datasource

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class OnboardingPreferences actual constructor() : KoinComponent {
    private val context: Context by inject()

    actual suspend fun isCompleted(): Boolean {
        val prefs = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("onboarding_completed", false)
    }

    actual suspend fun setCompleted() {
        val prefs = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("onboarding_completed", true).apply()
    }
}