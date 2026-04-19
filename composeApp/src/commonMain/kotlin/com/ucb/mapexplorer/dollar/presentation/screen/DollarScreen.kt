package com.ucb.mapexplorer.dollar.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.ucb.mapexplorer.dollar.presentation.viewmodel.DollarViewModel
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun DollarScreen(
    viewModel: DollarViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState()

    if(state.value.isLoading) {
        CircularProgressIndicator()
    } else {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Text("Cantidad de Registros ${state.value.list.size}")
        }
    }

}