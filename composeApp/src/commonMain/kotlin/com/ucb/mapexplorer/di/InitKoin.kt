package com.ucb.mapexplorer.di

import org.koin.core.module.Module

expect val platformModule: Module

fun getModules() = listOf(
    domainModule,
    presentationModule,
    dataModule,
    platformModule
)
