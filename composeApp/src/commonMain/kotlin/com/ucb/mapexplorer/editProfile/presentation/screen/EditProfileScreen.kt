package com.ucb.mapexplorer.editProfile.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ucb.designsystem.components.button.PrimaryButton
import com.ucb.designsystem.components.input.BasicInput
import com.ucb.designsystem.theme.AppTheme
import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import com.ucb.mapexplorer.profile.domain.model.*
import com.ucb.mapexplorer.editProfile.presentation.viewmodel.EditProfileViewModel
import com.ucb.mapexplorer.profile.presentation.composable.AvatarDisplay
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.text.style.TextAlign
import com.ucb.mapexplorer.editProfile.presentation.state.AvatarTab
import com.ucb.mapexplorer.editProfile.presentation.state.EditProfileEffect
import com.ucb.mapexplorer.editProfile.presentation.state.EditProfileEvent
import com.ucb.mapexplorer.profile.data.mapper.toResource
import org.jetbrains.compose.resources.DrawableResource
import com.ucb.mapexplorer.editProfile.presentation.composable.AvatarPartSelector
import com.ucb.mapexplorer.editProfile.presentation.composable.AvatarPartSelectorNullable
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                EditProfileEffect.NavigateBack    -> onCancel()
                EditProfileEffect.ShowSaveSuccess -> onSave()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        // ── TÍTULO ─────────────────────────────────────────
        Text(
            stringResource(Res.string.navigationBar_profile_textSelection).uppercase(),
            style = AppTheme.typography.headlineLarge,
            color = AppTheme.colors.textPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── DATOS DEL USUARIO ──────────────────────────────
        Text(
            stringResource(Res.string.editingProfile_subtittle_name),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.textSecondary
        )
        BasicInput(
            value = state.name,
            onValueChange = { viewModel.onEvent(EditProfileEvent.OnNameChange(it)) },
            label = "",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            stringResource(Res.string.editingProfile_subtittle_description),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.textSecondary
        )
        BasicInput(
            value = state.description,
            onValueChange = { viewModel.onEvent(EditProfileEvent.OnDescriptionChange(it)) },
            label = "",
            modifier = Modifier.fillMaxWidth(),
            singleLine = false
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── SECCIÓN AVATAR ─────────────────────────────────
        Text(
            stringResource(Res.string.editingProfile_subtittle_editingAvatar).uppercase(),
            style = AppTheme.typography.labelLarge,
            color = AppTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Preview del avatar combinado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(AppTheme.colors.surface),
            contentAlignment = Alignment.Center
        ) {
            AvatarDisplay(
                config = state.avatarConfig,
                size = 160.dp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs de categoría
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AvatarTab.entries.forEach { tab ->
                val isSelected = state.selectedTab == tab
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.onEvent(EditProfileEvent.OnTabSelected(tab)) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.surface,
                    contentColor = if (isSelected) Color.White else AppTheme.colors.textPrimary
                ) {
                    Text(
                        text = tab.label,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                        style = AppTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grid de opciones según el tab seleccionado
        when (state.selectedTab) {

            AvatarTab.BODY -> {
                AvatarPartSelector(
                    items = AvatarBody.entries.map { it.name to it.toResource() },
                    selectedName = state.avatarConfig.body.name,
                    onSelect = { name ->
                        val body = AvatarBody.valueOf(name)
                        viewModel.onEvent(EditProfileEvent.OnBodySelected(body))
                    }
                )
            }

            AvatarTab.HAT -> {
                // "Sin sombrero" primero
                val hatItems = listOf("NONE" to null) +
                        AvatarHat.entries
                            .filter { it != AvatarHat.NONE }
                            .map { it.name to it.toResource() }

                AvatarPartSelectorNullable(
                    items = hatItems,
                    selectedName = state.avatarConfig.hat.name,
                    onSelect = { name ->
                        val hat = AvatarHat.valueOf(name)
                        viewModel.onEvent(EditProfileEvent.OnHatSelected(hat))
                    }
                )
            }

            AvatarTab.ACCESSORY -> {
                val accItems = listOf("NONE" to null) +
                        AvatarAccessory.entries
                            .filter { it != AvatarAccessory.NONE }
                            .map { it.name to it.toResource() }

                AvatarPartSelectorNullable(
                    items = accItems,
                    selectedName = state.avatarConfig.accessory.name,
                    onSelect = { name ->
                        val acc = AvatarAccessory.valueOf(name)
                        viewModel.onEvent(EditProfileEvent.OnAccessorySelected(acc))
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── BOTONES ────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PrimaryButton(
                text = stringResource(Res.string.buttonText_cancel),
                onClick = { viewModel.onEvent(EditProfileEvent.OnCancelClick) },
                modifier = Modifier.weight(1f).height(48.dp),
                isPrimary = false
            )

            PrimaryButton(
                text = stringResource(Res.string.buttonText_save),
                onClick = { viewModel.onEvent(EditProfileEvent.OnSaveClick) },
                modifier = Modifier.weight(1f).height(48.dp),
                isLoading = state.isLoading,
                isPrimary = true
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
