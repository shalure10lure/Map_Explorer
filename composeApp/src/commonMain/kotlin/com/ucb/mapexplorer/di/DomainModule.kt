package com.ucb.mapexplorer.di

import com.ucb.mapexplorer.dollar.domain.usecase.CreateDollarUseCase
import com.ucb.mapexplorer.dollar.domain.usecase.GetDollarListUsecase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {

    singleOf(::GetDollarListUsecase)
    singleOf(::CreateDollarUseCase)

}
