package com.ucb.mapexplorer.profile.editProfile.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.designsystem.components.button.PrimaryButton
import com.ucb.designsystem.components.input.BasicInput
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.profile.editProfile.presentation.state.EditProfileEffect
import com.ucb.mapexplorer.profile.editProfile.presentation.state.EditProfileEvent
import com.ucb.mapexplorer.profile.editProfile.presentation.viewmodel.EditProfileViewModel
import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                EditProfileEffect.NavigateBack -> onCancel()
                EditProfileEffect.ShowSaveSuccess -> {
                    // Acción tras guardar
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(Res.string.editingProfile_subtittle_name),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.textSecondary
        )
        Text(
            text = state.name, 
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.editingProfile_subtittle_description),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.textSecondary
        )
        BasicInput(
            value = state.description,
            onValueChange = { viewModel.onEvent(EditProfileEvent.OnDescriptionChange(it)) },
            label = stringResource(Res.string.editingProfile_subtittle_description),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.editingProfile_subtittle_email),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.textSecondary
        )
        BasicInput(
            value = state.email,
            onValueChange = { viewModel.onEvent(EditProfileEvent.OnEmailChange(it)) },
            label = stringResource(Res.string.editingProfile_subtittle_email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.editingProfile_subtittle_editingAvatar),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        // Selector de Avatares Dinámico
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(AppTheme.colors.surface, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(Res.drawable.logo_map_explorer),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Selector de Avatares", 
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PrimaryButton(
                text = stringResource(Res.string.buttonText_cancel),
                onClick = { viewModel.onEvent(EditProfileEvent.OnCancelClick) },
                modifier = Modifier.weight(1f),
                isPrimary = true
            )
            PrimaryButton(
                text = stringResource(Res.string.buttonText_save),
                onClick = { viewModel.onEvent(EditProfileEvent.OnSaveClick) },
                modifier = Modifier.weight(1f),
                isPrimary = true
            )
        }
    }
}
