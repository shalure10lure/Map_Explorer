package com.ucb.mapexplorer.di

import com.ucb.mapexplorer.auth.data.datasource.FirebaseManager
import com.ucb.mapexplorer.auth.data.repository.AuthRepositoryImpl
import com.ucb.mapexplorer.auth.domain.repository.AuthRepository
import com.ucb.mapexplorer.dollar.data.datasource.DollarLocalDataSource
import com.ucb.mapexplorer.dollar.data.repository.DollarRepositoryImpl
import com.ucb.mapexplorer.dollar.data.service.DbService
import com.ucb.mapexplorer.dollar.domain.repository.DollarRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {

    singleOf(::DollarRepositoryImpl).bind<DollarRepository>()
    singleOf(::DbService).bind<DollarLocalDataSource>()


    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
    singleOf(::FirebaseManager)


}
