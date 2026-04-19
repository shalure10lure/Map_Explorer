package com.ucb.mapexplorer.di

import com.ucb.mapexplorer.dollar.presentation.viewmodel.DollarViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {

    viewModelOf(::DollarViewModel)


}
