package com.ucb.mapexplorer.di

import com.ucb.mapexplorer.auth.presentation.login.viewmodel.LoginViewModel
import com.ucb.mapexplorer.auth.presentation.register.viewmodel.RegisterViewModel
import com.ucb.mapexplorer.profile.editProfile.presentation.viewmodel.EditProfileViewModel
import com.ucb.mapexplorer.profile.ownProfile.presentation.viewmodel.OwnProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::EditProfileViewModel)
    viewModelOf(::OwnProfileViewModel)
}