package com.ucb.mapexplorer.di

import com.ucb.mapexplorer.auth.data.datasource.FirebaseManager
import com.ucb.mapexplorer.auth.data.repository.AuthRepositoryImpl
import com.ucb.mapexplorer.auth.domain.repository.AuthRepository
import com.ucb.mapexplorer.map.data.datasource.MapLocalDataSource
import com.ucb.mapexplorer.map.data.datasource.MapRemoteDataSource
import com.ucb.mapexplorer.map.data.repository.MapRepositoryImpl
import com.ucb.mapexplorer.map.data.service.LocalitationService
import com.ucb.mapexplorer.map.domain.repository.MapRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
    singleOf(::FirebaseManager)
    singleOf(::MapLocalDataSource)
    singleOf(::MapRemoteDataSource)
    singleOf(::LocalitationService)
    singleOf(::MapRepositoryImpl).bind<MapRepository>()
}
