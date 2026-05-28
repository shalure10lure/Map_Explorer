package com.ucb.mapexplorer.onboarding.di

import com.ucb.mapexplorer.onboarding.data.datasource.OnboardingPreferences
import com.ucb.mapexplorer.onboarding.data.datasource.RemoteConfigDataSource
import com.ucb.mapexplorer.onboarding.data.repository.OnboardingRepositoryImpl
import com.ucb.mapexplorer.onboarding.domain.repository.OnboardingRepository
import com.ucb.mapexplorer.onboarding.presentation.viewmodel.OnboardingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val onboardingModule = module {
    single { RemoteConfigDataSource() }
    single { OnboardingPreferences() }
    single<OnboardingRepository> { OnboardingRepositoryImpl(get(), get()) }
    viewModel { OnboardingViewModel(get()) }
}