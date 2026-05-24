package com.ucb.mapexplorer.map.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.designsystem.components.button.PrimaryButton
import com.ucb.designsystem.theme.AppTheme
import com.ucb.designsystem.theme.ThemeMode
import com.ucb.mapexplorer.core.*
import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            MapSettingsContent()
        },
        sheetPeekHeight = 80.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = AppTheme.colors.surface,
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle(
                color = AppTheme.colors.textSecondary.copy(alpha = 0.5f)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).background(AppTheme.colors.background)) {
            MapViewContainer(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun MapSettingsContent() {
    // Valores globales actuales (reales)
    val globalLanguage = LocalAppLanguage.current
    val globalTheme = LocalThemeMode.current
    
    // Controladores globales para aplicar cambios
    val changeLanguage = LocalLanguageController.current
    val changeTheme = LocalThemeController.current

    // ESTADO TEMPORAL (Borrador del usuario)
    var tempLanguage by remember(globalLanguage) { mutableStateOf(globalLanguage) }
    var tempTheme by remember(globalTheme) { mutableStateOf(globalTheme) }

    // Verificamos si el usuario ha movido algo respecto a lo guardado
    val hasChanges = tempLanguage != globalLanguage || tempTheme != globalTheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.surface)
            .padding(horizontal = 24.dp)
            .padding(bottom = 40.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // --- SECCIÓN: CONFIGURACIÓN ---
        SectionHeader(stringResource(Res.string.moreOptions_tittle_configuration))
        
        Spacer(modifier = Modifier.height(8.dp))

        // Selector de Idioma
        Text(
            text = stringResource(Res.string.moreOptions_subtittle_language),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingChip(
                text = stringResource(Res.string.moreOptions_optionText_spanish),
                isSelected = tempLanguage == AppLanguage.SPANISH,
                onClick = { tempLanguage = AppLanguage.SPANISH }
            )
            SettingChip(
                text = stringResource(Res.string.moreOptions_optionText_english),
                isSelected = tempLanguage == AppLanguage.ENGLISH,
                onClick = { tempLanguage = AppLanguage.ENGLISH }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        // Selector de Tema
        Text(
            text = stringResource(Res.string.moreOptions_subtittle_theme),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingChip(
                text = stringResource(Res.string.moreOptions_optionText_bright),
                isSelected = tempTheme == ThemeMode.LIGHT,
                onClick = { tempTheme = ThemeMode.LIGHT }
            )
            SettingChip(
                text = stringResource(Res.string.moreOptions_optionText_dark),
                isSelected = tempTheme == ThemeMode.DARK,
                onClick = { tempTheme = ThemeMode.DARK }
            )
        }
        
        // BOTONES DE ACCIÓN (Solo aparecen si hay cambios pendientes)
        if (hasChanges) {
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // BOTÓN REVERTIR
                OutlinedButton(
                    onClick = {
                        tempLanguage = globalLanguage
                        tempTheme = globalTheme
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, AppTheme.colors.border),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppTheme.colors.textPrimary
                    )
                ) {
                    Text(stringResource(Res.string.buttonText_cancel), style = AppTheme.typography.labelLarge)
                }

                // BOTÓN GUARDAR
                PrimaryButton(
                    text = stringResource(Res.string.buttonText_save),
                    onClick = {
                        if (tempLanguage != globalLanguage) changeLanguage(tempLanguage)
                        if (tempTheme != globalTheme) changeTheme(tempTheme)
                    },
                    modifier = Modifier.weight(1f).height(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- SECCIÓN: MIS GUARDADOS ---
        SectionHeader(stringResource(Res.string.moreOptions_tittle_mySaves))

    }
}

@Composable
private fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title.uppercase(),
            style = AppTheme.typography.labelLarge,
            color = AppTheme.colors.textSecondary,
            letterSpacing = 1.2.sp
        )
        Spacer(modifier = Modifier.width(12.dp))
        com.ucb.designsystem.components.divider.HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = AppTheme.colors.border.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun SettingChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.textPrimary.copy(alpha = 0.05f),
        contentColor = if (isSelected) Color.White else AppTheme.colors.textPrimary,
        border = if (!isSelected) BorderStroke(1.dp, AppTheme.colors.border.copy(alpha = 0.3f)) else null
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            style = AppTheme.typography.bodyMedium
        )
    }
}
