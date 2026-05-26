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
import com.ucb.designsystem.theme.AppTheme
import com.ucb.designsystem.theme.ThemeMode
import com.ucb.mapexplorer.core.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )

    // fillMaxSize sin padding externo — el scaffold vive dentro del Box de MainScreen
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
        },
        // Clave: sin color de fondo en el scaffold para que no tape nada
        containerColor = Color.Transparent,
        contentColor = Color.Transparent,
    ) {
        // No usar innerPadding aquí — causaba que el mapa empujara la TopBar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.background)
        ) {
            MapViewContainer(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun MapSettingsContent() {
    val onThemeChange = LocalThemeController.current
    val currentTheme = LocalThemeMode.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Configuración del Mapa",
            style = AppTheme.typography.headlineLarge,
            color = AppTheme.colors.textPrimary,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ThemeOption(
                label = "Claro",
                isSelected = currentTheme == ThemeMode.LIGHT,
                onClick = { onThemeChange(ThemeMode.LIGHT) }
            )
            ThemeOption(
                label = "Oscuro",
                isSelected = currentTheme == ThemeMode.DARK,
                onClick = { onThemeChange(ThemeMode.DARK) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ThemeOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(120.dp)
            .height(48.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.surface,
        border = if (!isSelected) BorderStroke(1.dp, AppTheme.colors.border) else null,
        tonalElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = if (isSelected) Color.White else AppTheme.colors.textPrimary,
                style = AppTheme.typography.labelLarge
            )
        }
    }
}
