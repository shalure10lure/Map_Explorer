package com.ucb.mapexplorer.auth.presentation.register.screen

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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ucb.mapexplorer.auth.presentation.register.state.RegisterEffect
import com.ucb.mapexplorer.auth.presentation.register.state.RegisterEvent
import com.ucb.mapexplorer.auth.presentation.register.viewmodel.RegisterViewModel
import com.ucb.mapexplorer.navigation.NavRoute
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    viewModel: RegisterViewModel = koinViewModel()
) {

    val state by viewModel.state.collectAsState()

    // EFECTOS
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {

                RegisterEffect.NavigateToLogin -> {
                    navController.navigate(NavRoute.Login)
                }

                is RegisterEffect.ShowError -> {
                    snackbarHostState.showSnackbar(
                        effect.message,
                        withDismissAction = true
                    )
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

        Text("Register")

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = state.username,
            onValueChange = {
                viewModel.onEvent(RegisterEvent.OnUsernameChanged(it))
            },
            label = { Text("Username") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = state.email,
            onValueChange = {
                viewModel.onEvent(RegisterEvent.OnEmailChanged(it))
            },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = state.password,
            onValueChange = {
                viewModel.onEvent(RegisterEvent.OnPasswordChanged(it))
            },
            label = { Text("Password") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = state.confirmPassword,
            onValueChange = {
                viewModel.onEvent(RegisterEvent.OnConfirmPasswordChanged(it))
            },
            label = { Text("Confirm Password") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = state.description,
            onValueChange = {
                viewModel.onEvent(RegisterEvent.OnDescriptionChanged(it))
            },
            label = { Text("Descripción") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.onEvent(RegisterEvent.OnClick)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "¿Ya tienes cuenta? Iniciar sesión",
            modifier = Modifier.clickable {
                viewModel.onEvent(RegisterEvent.OnClickLogin)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
        }
    }
}