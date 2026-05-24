package com.ucb.mapexplorer.profile.ownProfile.presentation.state

data class OwnProfileUIState(
    val userName: String = "Usuario",
    val description: String = "Descripción del usuario",
    val level: Int = 5,
    val email: String = "usuario@gmail.com",
    val friends: List<String> = listOf("amigo1", "amigo2", "amigo3", "amigo4")
)


