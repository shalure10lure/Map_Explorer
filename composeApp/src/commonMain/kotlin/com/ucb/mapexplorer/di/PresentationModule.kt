package com.ucb.mapexplorer.di

import com.ucb.mapexplorer.auth.presentation.login.viewmodel.LoginViewModel
import com.ucb.mapexplorer.auth.presentation.register.viewmodel.RegisterViewModel
import com.ucb.mapexplorer.dollar.presentation.viewmodel.DollarViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {

    viewModelOf(::DollarViewModel)

    viewModelOf(::LoginViewModel)

    viewModelOf(::RegisterViewModel)
}
