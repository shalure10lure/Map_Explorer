package com.ucb.mapexplorer.onboarding.data.datasource

import platform.Foundation.NSUserDefaults

actual class OnboardingPreferences actual constructor() {

    private val defaults = NSUserDefaults.standardUserDefaults

    actual suspend fun isCompleted(): Boolean {
        return defaults.boolForKey("onboarding_completed")
    }

    actual suspend fun setCompleted() {
        defaults.setBool(true, forKey = "onboarding_completed")
        defaults.synchronize()
    }
}
