package com.ucb.mapexplorer.di

import com.ucb.mapexplorer.auth.presentation.login.viewmodel.LoginViewModel
import com.ucb.mapexplorer.auth.presentation.register.viewmodel.RegisterViewModel
import com.ucb.mapexplorer.map.presentation.viewmodel.MapViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    val presentationModule = module {
        viewModel { (uid: String) ->
            MapViewModel(
                uid = uid,
                getCurrentLocationUseCase = get(),
                unlockTileUseCase = get(),
                getDiscoveredTilesUseCase = get()
            )
        }
    }

}
