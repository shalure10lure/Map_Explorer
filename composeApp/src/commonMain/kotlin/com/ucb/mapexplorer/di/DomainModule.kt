package com.ucb.mapexplorer.di

import com.ucb.mapexplorer.auth.domain.usecase.LoginUseCase
import com.ucb.mapexplorer.auth.domain.usecase.RegisterUseCase
import com.ucb.mapexplorer.map.domain.usecase.GetCurrentLocationUseCase
import com.ucb.mapexplorer.map.domain.usecase.GetDiscoveredTilesUseCase
import com.ucb.mapexplorer.map.domain.usecase.UnlockTileUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    singleOf(::LoginUseCase)
    singleOf(::RegisterUseCase)
    singleOf(::GetDiscoveredTilesUseCase)
    singleOf(::GetCurrentLocationUseCase)
    singleOf(::UnlockTileUseCase)

}
