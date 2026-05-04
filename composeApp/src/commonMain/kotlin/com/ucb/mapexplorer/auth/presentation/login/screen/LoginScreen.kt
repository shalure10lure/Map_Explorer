package com.ucb.mapexplorer.auth.presentation.login.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ucb.mapexplorer.auth.presentation.login.state.LoginEffect
import com.ucb.mapexplorer.auth.presentation.login.state.LoginEvent
import com.ucb.mapexplorer.auth.presentation.login.viewmodel.LoginViewModel
import com.ucb.mapexplorer.navigation.NavRoute

import mapexplorer.composeapp.generated.resources.Res
import mapexplorer.composeapp.generated.resources.login_btn
import mapexplorer.composeapp.generated.resources.login_email
import mapexplorer.composeapp.generated.resources.login_no_account
import mapexplorer.composeapp.generated.resources.login_password
import mapexplorer.composeapp.generated.resources.login_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = koinViewModel()
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {

                LoginEffect.NavigateToHome -> {
                   // navController.navigate(NavRoute.Home)
                }

                LoginEffect.NavigateToRegister -> {
                   navController.navigate(NavRoute.Register)
                }

                is LoginEffect.ShowError -> {
                    /*snackbarHostState.showSnackbar(
                        effect.message,
                        withDismissAction = true
                    )*/
                }

            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(stringResource(Res.string.login_title))

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = state.email,
            onValueChange = {
                viewModel.onEvent(LoginEvent.OnEmailChanged(it))
            },
            label = {
                Text(stringResource(Res.string.login_email))
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = state.password,
            onValueChange = {
                viewModel.onEvent(LoginEvent.OnPasswordChanged(it))
            },
            label = {
                Text(stringResource(Res.string.login_password))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.onEvent(LoginEvent.OnClick)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.login_btn))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(Res.string.login_no_account),
            modifier = Modifier.clickable {
                viewModel.onEvent(LoginEvent.OnClickRegister)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
        }
    }
}