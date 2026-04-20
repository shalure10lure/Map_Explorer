package com.ucb.mapexplorer.di

import com.ucb.mapexplorer.auth.domain.usecase.LoginUseCase
import com.ucb.mapexplorer.auth.domain.usecase.RegisterUseCase
import com.ucb.mapexplorer.dollar.domain.usecase.CreateDollarUseCase
import com.ucb.mapexplorer.dollar.domain.usecase.GetDollarListUsecase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {

    singleOf(::GetDollarListUsecase)
    singleOf(::CreateDollarUseCase)

    singleOf(::LoginUseCase)
    singleOf(::RegisterUseCase)

}
