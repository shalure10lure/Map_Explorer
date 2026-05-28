package com.ucb.mapexplorer.di

import com.ucb.mapexplorer.onboarding.di.onboardingModule
import org.koin.core.module.Module

expect val platformModule: Module

fun getModules() = listOf(
    domainModule,
    presentationModule,
    dataModule,
    platformModule,
    onboardingModule
)
