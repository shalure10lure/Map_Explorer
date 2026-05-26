package com.ucb.mapexplorer.di

import com.ucb.mapexplorer.auth.domain.usecase.LoginUseCase
import com.ucb.mapexplorer.auth.domain.usecase.RegisterUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    singleOf(::LoginUseCase)
    singleOf(::RegisterUseCase)
}
