package com.ucb.mapexplorer.profile.domain.model

enum class AvatarBody(val resourceName: String, val displayName: String) {
    GATO("avatar_cu_gato", "Gato"),
    GALLINA("avatar_cu_gall", "Gallina"),
    PATO("avatar_cu_pato", "Pato")
}

enum class AvatarHat(val resourceName: String, val displayName: String) {
    NONE("", "Sin sombrero"),
    BANANA("avatar_som_banan", "Banana"),
    HOJA("avatar_som_hoja", "Hoja"),
    ICE_CREAM("avatar_som_ice_c", "Helado")
}

enum class AvatarAccessory(val resourceName: String, val displayName: String) {
    NONE("", "Sin accesorio"),
    BOOBA("avatar_acc_booba", "Booba Tea"),
    CAKE("avatar_acc_cake", "Cake"),
    MONSTER("avatar_acc_monster", "Monster")
}

data class AvatarConfigModel(
    val body: AvatarBody = AvatarBody.GATO,
    val hat: AvatarHat = AvatarHat.NONE,
    val accessory: AvatarAccessory = AvatarAccessory.NONE
) {
    // Genera un ID único para guardar en Firebase/Room
    fun toId(): String = "${body.name}|${hat.name}|${accessory.name}"

    companion object {
        fun fromId(id: String): AvatarConfigModel {
            val parts = id.split("|")
            return AvatarConfigModel(
                body      = AvatarBody.entries.find { it.name == parts.getOrNull(0) } ?: AvatarBody.GATO,
                hat       = AvatarHat.entries.find { it.name == parts.getOrNull(1) } ?: AvatarHat.NONE,
                accessory = AvatarAccessory.entries.find { it.name == parts.getOrNull(2) } ?: AvatarAccessory.NONE
            )
        }
    }
}