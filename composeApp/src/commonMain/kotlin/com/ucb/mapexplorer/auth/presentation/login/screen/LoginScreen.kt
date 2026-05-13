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
import mapexplorer.composeapp.generated.resources.logo_map_explorer
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ucb.designsystem.components.button.PrimaryButton
import com.ucb.designsystem.components.input.BasicInput
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
                LoginEffect.NavigateToHome -> { /* Implementar home */ }
                LoginEffect.NavigateToRegister -> { navController.navigate(NavRoute.Register) }
                is LoginEffect.ShowError -> { /* Mostrar error */ }
            }
        }
    }

    // CONTENEDOR PRINCIPAL (Fondo de toda la pantalla)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background) // Este debe ser el color claro/rosado
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // 1. LOGO (Fuera de la Card, arriba)
        Image(
            painter = painterResource(Res.drawable.logo_map_explorer),
            contentDescription = null,
            modifier = Modifier.size(160.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. LA TARJETA BLANCA (ElevatedCard)
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(stringResource(Res.string.login_title), style = AppTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(16.dp))

                // Email con etiqueta arriba
                Text(stringResource(Res.string.login_email), style = AppTheme.typography.bodyMedium)
                BasicInput(
                    value = state.email,
                    onValueChange = { viewModel.onEvent(LoginEvent.OnEmailChanged(it)) },
                    label = stringResource(Res.string.login_email_placeholder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password con etiqueta arriba
                Text(stringResource(Res.string.login_password), style = AppTheme.typography.bodyMedium)
                BasicInput(
                    value = state.password,
                    onValueChange = { viewModel.onEvent(LoginEvent.OnPasswordChanged(it)) },
                    label = stringResource(Res.string.login_password_placeholder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // BOTÓN SIGN IN (Negro #2C2C2C)
                PrimaryButton(
                    text = stringResource(Res.string.login_btn),
                    onClick = { viewModel.onEvent(LoginEvent.OnClick) },
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = state.isLoading,
                    isPrimary = true
                )

                // OLVIDÉ MI CONTRASEÑA
                Text(
                    text = stringResource(Res.string.login_forgot_password),
                    style = AppTheme.typography.bodySmall.copy(
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .clickable { /* Acción */ }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // SECCIÓN "CREAR CUENTA"
                Text(
                    text = stringResource(Res.string.login_no_account_question),
                    style = AppTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                PrimaryButton(
                    text = stringResource(Res.string.login_create_account),
                    onClick = { viewModel.onEvent(LoginEvent.OnClickRegister) },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true
                )
            }
        }
    }
}
