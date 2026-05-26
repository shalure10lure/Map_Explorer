package com.ucb.mapexplorer.auth.presentation.register.screen

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ucb.designsystem.components.button.PrimaryButton
import com.ucb.designsystem.components.input.BasicInput
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.auth.presentation.register.state.*
import com.ucb.mapexplorer.auth.presentation.register.viewmodel.RegisterViewModel
import com.ucb.mapexplorer.navigation.NavRoute
import com.ucb.mapexplorer.profile.domain.model.*
import com.ucb.mapexplorer.profile.presentation.composable.*
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

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                RegisterEffect.NavigateToLogin ->
                    navController.navigate(NavRoute.Login) {
                        popUpTo(NavRoute.Register) { inclusive = true }
                    }
                RegisterEffect.NavigateToHome ->
                    navController.navigate(NavRoute.Explanation1) {
                        popUpTo(NavRoute.Register) { inclusive = true }
                    }
                is RegisterEffect.ShowError ->
                    snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    // Indicador de progreso arriba
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        // Barra de progreso de pasos
        StepProgressBar(
            currentStep = state.currentStep,
            totalSteps  = 2
        )

        // Contenido del paso actual
        AnimatedContent(
            targetState = state.currentStep,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                } else {
                    slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
                }
            }
        ) { step ->
            when (step) {
                1 -> RegisterStep1(
                    state   = state,
                    onEvent = viewModel::onEvent,
                    snackbarHostState = snackbarHostState
                )
                2 -> RegisterStep2(
                    state   = state,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}

// ── BARRA DE PROGRESO ──────────────────────────────────────
@Composable
private fun StepProgressBar(currentStep: Int, totalSteps: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(totalSteps) { index ->
            val isCompleted = index < currentStep
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        color = if (isCompleted) AppTheme.colors.primary
                        else AppTheme.colors.border,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

// ── PASO 1: Datos básicos ──────────────────────────────────
@Composable
private fun RegisterStep1(
    state: RegisterUIState,
    onEvent: (RegisterEvent) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.logo_map_explorer),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = AppTheme.colors.surface
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                Text(
                    stringResource(Res.string.signIn_tittle),
                    style = AppTheme.typography.headlineLarge,
                    color = AppTheme.colors.textPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Nombre de usuario
                Text(
                    stringResource(Res.string.signIn_subtittle_username),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textSecondary
                )
                BasicInput(
                    value = state.username,
                    onValueChange = { onEvent(RegisterEvent.OnUsernameChanged(it)) },
                    label = "Elegir un nombre de usuario",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Email
                Text(
                    stringResource(Res.string.signIn_subtittle_email),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textSecondary
                )
                BasicInput(
                    value = state.email,
                    onValueChange = { onEvent(RegisterEvent.OnEmailChanged(it)) },
                    label = "ejemplo@mail.com",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Contraseña
                Text(
                    stringResource(Res.string.signIn_subtittle_password),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textSecondary
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { onEvent(RegisterEvent.OnPasswordChanged(it)) },
                    label = { Text("Al menos 8 caracteres") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Confirmar contraseña
                Text(
                    stringResource(Res.string.signIn_subtittle_confirmPassword),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textSecondary
                )
                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = { onEvent(RegisterEvent.OnConfirmPasswordChanged(it)) },
                    label = { Text("Mínimo 8 caracteres") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                PrimaryButton(
                    text = stringResource(Res.string.buttonText_continue),
                    onClick = { onEvent(RegisterEvent.OnNextStep) },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(Res.string.signIn_text_alreadyHaveAccount_question),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEvent(RegisterEvent.OnClickLogin) }
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

// ── PASO 2: Datos opcionales + Avatar ─────────────────────
@Composable
private fun RegisterStep2(
    state: RegisterUIState,
    onEvent: (RegisterEvent) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.logo_map_explorer),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = AppTheme.colors.surface
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                Text(
                    stringResource(Res.string.optionalData_tittle),
                    style = AppTheme.typography.headlineLarge,
                    color = AppTheme.colors.textPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Descripción (opcional)
                Text(
                    stringResource(Res.string.optionalData_subtittle_description),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textSecondary
                )
                BasicInput(
                    value = state.description,
                    onValueChange = { onEvent(RegisterEvent.OnDescriptionChanged(it)) },
                    label = "Cuéntanos sobre ti...",
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Edad con Slider desde 15
                Text(
                    stringResource(Res.string.optionalData_subtittle_age),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textSecondary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slider(
                        value = state.age.toFloat(),
                        onValueChange = { onEvent(RegisterEvent.OnAgeChanged(it.toInt())) },
                        valueRange = 15f..100f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = AppTheme.colors.primary,
                            activeTrackColor = AppTheme.colors.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${state.age}",
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.textPrimary,
                        modifier = Modifier.width(32.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar
                Text(
                    stringResource(Res.string.optionalData_subtittle_editAvatar),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                AvatarEditorCompact(
                    avatarConfig = state.avatarConfig,
                    selectedTab  = state.selectedTab,
                    onBodySelected = { onEvent(RegisterEvent.OnBodySelected(it)) },
                    onHatSelected  = { onEvent(RegisterEvent.OnHatSelected(it)) },
                    onAccSelected  = { onEvent(RegisterEvent.OnAccessorySelected(it)) },
                    onTabSelected  = { onEvent(RegisterEvent.OnAvatarTabSelected(it)) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                PrimaryButton(
                    text = stringResource(Res.string.optionalData_buttonText_signIn),
                    onClick = { onEvent(RegisterEvent.OnClick) },
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = state.isLoading,
                    isPrimary = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { onEvent(RegisterEvent.OnBackStep) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "← Volver al paso anterior",
                        color = AppTheme.colors.textSecondary,
                        style = AppTheme.typography.bodySmall
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}