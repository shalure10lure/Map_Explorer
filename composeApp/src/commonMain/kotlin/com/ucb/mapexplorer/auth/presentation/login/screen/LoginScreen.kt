package com.ucb.mapexplorer.auth.presentation.login.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ucb.designsystem.components.button.PrimaryButton
import com.ucb.designsystem.components.input.BasicInput
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.auth.presentation.login.state.LoginEffect
import com.ucb.mapexplorer.auth.presentation.login.state.LoginEvent
import com.ucb.mapexplorer.auth.presentation.login.viewmodel.LoginViewModel
import com.ucb.mapexplorer.navigation.NavRoute
import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
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
                    navController.navigate(NavRoute.Map) {
                        popUpTo(NavRoute.Login) { inclusive = true }
                    }
                }
                LoginEffect.NavigateToRegister -> {
                    navController.navigate(NavRoute.Register)
                }
                is LoginEffect.ShowError -> { /* Mostrar error */ }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(Res.drawable.logo_map_explorer),
            contentDescription = null,
            modifier = Modifier.size(160.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = AppTheme.colors.surface // DINÁMICO
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = stringResource(Res.string.login_tittle), 
                    style = AppTheme.typography.headlineLarge,
                    color = AppTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(Res.string.login_subtittle_email), 
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textPrimary
                )
                BasicInput(
                    value = state.email,
                    onValueChange = { viewModel.onEvent(LoginEvent.OnEmailChanged(it)) },
                    label = "", 
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(Res.string.login_subtittle_password), 
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textPrimary
                )
                BasicInput(
                    value = state.password,
                    onValueChange = { viewModel.onEvent(LoginEvent.OnPasswordChanged(it)) },
                    label = "", 
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                PrimaryButton(
                    text = stringResource(Res.string.login_buttonText_login),
                    onClick = { viewModel.onEvent(LoginEvent.OnClick) },
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = state.isLoading,
                    isPrimary = true
                )

                Text(
                    text = stringResource(Res.string.login_forgotPassword_question),
                    style = AppTheme.typography.bodySmall.copy(
                        textDecoration = TextDecoration.Underline
                    ),
                    color = AppTheme.colors.textSecondary,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .clickable { /* Acción */ }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = stringResource(Res.string.login_dontHaveAccount_question),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                PrimaryButton(
                    text = stringResource(Res.string.login_buttonText_createAccount),
                    onClick = { viewModel.onEvent(LoginEvent.OnClickRegister) },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true
                )
            }
        }
    }
}
