package com.ucb.mapexplorer.auth.presentation.register.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ucb.designsystem.components.button.PrimaryButton
import com.ucb.designsystem.components.input.BasicInput
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.auth.presentation.register.state.RegisterEffect
import com.ucb.mapexplorer.auth.presentation.register.state.RegisterEvent
import com.ucb.mapexplorer.auth.presentation.register.viewmodel.RegisterViewModel
import com.ucb.mapexplorer.navigation.NavRoute 
import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                RegisterEffect.NavigateToLogin -> {
                    navController.navigate(NavRoute.Login) {
                        popUpTo(NavRoute.Register) { inclusive = true }
                    }
                }
                RegisterEffect.NavigateToHome -> {
                    // Cambiado para que vaya a las explicaciones después de registrarse
                    navController.navigate(NavRoute.Explanation1) {
                        popUpTo(NavRoute.Register) { inclusive = true }
                    }
                }
                is RegisterEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(Res.drawable.logo_map_explorer),
            contentDescription = null,
            modifier = Modifier.size(140.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = AppTheme.colors.surface
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                Text(
                    text = stringResource(Res.string.signIn_tittle), 
                    style = AppTheme.typography.headlineLarge,
                    color = AppTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 1. USERNAME
                Text(
                    text = stringResource(Res.string.signIn_subtittle_username), 
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textPrimary
                )
                BasicInput(
                    value = state.username,
                    onValueChange = { viewModel.onEvent(RegisterEvent.OnUsernameChanged(it)) },
                    label = "",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. EMAIL
                Text(
                    text = stringResource(Res.string.signIn_subtittle_email), 
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textPrimary
                )
                BasicInput(
                    value = state.email,
                    onValueChange = { viewModel.onEvent(RegisterEvent.OnEmailChanged(it)) },
                    label = "",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3. PASSWORD
                Text(
                    text = stringResource(Res.string.signIn_subtittle_password), 
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textPrimary
                )
                BasicInput(
                    value = state.password,
                    onValueChange = { viewModel.onEvent(RegisterEvent.OnPasswordChanged(it)) },
                    label = "",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 4. CONFIRM PASSWORD
                Text(
                    text = stringResource(Res.string.signIn_subtittle_confirmPassword), 
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textPrimary
                )
                BasicInput(
                    value = state.confirmPassword,
                    onValueChange = { viewModel.onEvent(RegisterEvent.OnConfirmPasswordChanged(it)) },
                    label = "",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 5. DESCRIPTION
                Text(
                    text = stringResource(Res.string.optionalData_subtittle_description), 
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textPrimary
                )
                BasicInput(
                    value = state.description,
                    onValueChange = { viewModel.onEvent(RegisterEvent.OnDescriptionChanged(it)) },
                    label = "",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // BOTÓN REGISTRAR
                PrimaryButton(
                    text = stringResource(Res.string.optionalData_buttonText_signIn),
                    onClick = { viewModel.onEvent(RegisterEvent.OnClick) },
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = state.isLoading,
                    isPrimary = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // YA TENGO CUENTA
                Text(
                    text = stringResource(Res.string.signIn_text_alreadyHaveAccount_question),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textSecondary,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable { viewModel.onEvent(RegisterEvent.OnClickLogin) }
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}
