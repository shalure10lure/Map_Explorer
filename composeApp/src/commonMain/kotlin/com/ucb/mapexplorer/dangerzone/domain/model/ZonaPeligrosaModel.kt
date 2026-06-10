package com.ucb.mapexplorer.dangerzone.domain.model

data class ZonaPeligrosaModel(
    val zonaId: String,
    val nombre: String,
    val descripcion: String,
    val latitud: Double,
    val longitud: Double,
    val radio: Double,
    val nivel: NivelPeligro,
    val tipo: String,
    val activa: Boolean = true
)

enum class NivelPeligro(val label: String, val emoji: String, val colorHex: String) {
    BAJO("Bajo", "⚠️", "#FFC107"),
    MEDIO("Medio", "🟠", "#FF9800"),
    ALTO("Alto", "🔴", "#F44336"),
    CRITICO("Crítico", "💀", "#B71C1C")
}

fun String.toNivelPeligro(): NivelPeligro = when (this.lowercase()) {
    "bajo"    -> NivelPeligro.BAJO
    "medio"   -> NivelPeligro.MEDIO
    "alto"    -> NivelPeligro.ALTO
    "critico" -> NivelPeligro.CRITICO
    else      -> NivelPeligro.MEDIO
}
