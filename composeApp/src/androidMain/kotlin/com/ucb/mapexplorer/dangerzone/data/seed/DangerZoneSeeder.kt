package com.ucb.mapexplorer.dangerzone.data.seed

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

object DangerZoneSeeder {

    suspend fun seedDangerZones() {
        try {
            val db = FirebaseDatabase.getInstance().reference

            // Solo insertar si el nodo está vacío
            val existing = db.child("zonas_peligrosas").get().await()
            if (existing.exists() && existing.childrenCount > 0) {
                println("✅ Zonas ya existen, skip seed")
                return
            }
            val zonas = mapOf(

                "zona_mercado_10feb_cbba" to mapOf(
                    "nombre" to "Mercado 10 de Febrero - Zona Norte",
                    "descripcion" to "Robos frecuentes a transeuntes, carteristas y motoladrones activos. Alta concurrencia con poca iluminacion nocturna.",
                    "latitud" to -17.3770,
                    "longitud" to -66.1520,
                    "radio" to 300.0,
                    "nivel" to "alto",
                    "tipo" to "robo",
                    "activa" to true
                ),

                "zona_la_cancha_cbba" to mapOf(
                    "nombre" to "La Cancha - Mercado Central",
                    "descripcion" to "Mercado mas peligroso segun la FELCC. Carteristas, motoladrones y robos al paso. Multiples tipos de ladrones activos.",
                    "latitud" to -17.3935,
                    "longitud" to -66.1569,
                    "radio" to 400.0,
                    "nivel" to "critico",
                    "tipo" to "robo",
                    "activa" to true
                ),

                "zona_calle_espana_cbba" to mapOf(
                    "nombre" to "Calle Espana - Centro Historico",
                    "descripcion" to "Zona roja por pandillas, droga y locales nocturnos. Peleas y asaltos desde las 22:00.",
                    "latitud" to -17.3940,
                    "longitud" to -66.1560,
                    "radio" to 150.0,
                    "nivel" to "alto",
                    "tipo" to "pandillas",
                    "activa" to true
                ),

                "zona_aroma_ayacucho_cbba" to mapOf(
                    "nombre" to "Av. Aroma y Ayacucho - Plaza San Sebastian",
                    "descripcion" to "Personas en situacion de calle que cometen robos. Estafadores y falsos taxistas.",
                    "latitud" to -17.3920,
                    "longitud" to -66.1545,
                    "radio" to 200.0,
                    "nivel" to "medio",
                    "tipo" to "robo",
                    "activa" to true
                ),

                "zona_terminal_cbba" to mapOf(
                    "nombre" to "Terminal de Buses Cochabamba",
                    "descripcion" to "Descuidistas y pildoritos activos. Mayor riesgo en horario nocturno.",
                    "latitud" to -17.3800,
                    "longitud" to -66.1580,
                    "radio" to 250.0,
                    "nivel" to "medio",
                    "tipo" to "robo",
                    "activa" to true
                ),

                "zona_av_america_norte_cbba" to mapOf(
                    "nombre" to "Avenida America Oeste - Zona Norte",
                    "descripcion" to "Motoladrones identificados por la FELCC. Roban celulares desde motos en movimiento.",
                    "latitud" to -17.3750,
                    "longitud" to -66.1480,
                    "radio" to 350.0,
                    "nivel" to "alto",
                    "tipo" to "motoladron",
                    "activa" to true
                ),

                "zona_parque_pulpo_cbba" to mapOf(
                    "nombre" to "Parque El Pulpo - Zona Norte",
                    "descripcion" to "Punto de reunion de pandillas. Robos y atracos frecuentes especialmente de noche.",
                    "latitud" to -17.3700,
                    "longitud" to -66.1500,
                    "radio" to 200.0,
                    "nivel" to "alto",
                    "tipo" to "pandillas",
                    "activa" to true
                ),

                "zona_cala_cala_cbba" to mapOf(
                    "nombre" to "Cala Cala - Av. Humboldt puente",
                    "descripcion" to "Pandillas, robos y venta de drogas. Asaltos con armas blancas.",
                    "latitud" to -17.3680,
                    "longitud" to -66.1450,
                    "radio" to 300.0,
                    "nivel" to "medio",
                    "tipo" to "robo",
                    "activa" to true
                ),

                "zona_melchor_perez_cbba" to mapOf(
                    "nombre" to "Av. Melchor Perez - Circunvalacion",
                    "descripcion" to "Asaltos con armas blancas y robos nocturnos frecuentes.",
                    "latitud" to -17.3850,
                    "longitud" to -66.1420,
                    "radio" to 250.0,
                    "nivel" to "medio",
                    "tipo" to "robo",
                    "activa" to true
                ),

                "zona_parque_mariscal_cbba" to mapOf(
                    "nombre" to "Parque Mariscal Santa Cruz",
                    "descripcion" to "Zona de conformacion de pandillas y venta de drogas.",
                    "latitud" to -17.3960,
                    "longitud" to -66.1570,
                    "radio" to 200.0,
                    "nivel" to "medio",
                    "tipo" to "pandillas",
                    "activa" to true
                ),

                "zona_test_cbba_centro" to mapOf(
                    "nombre" to "Zona Test - Cbba Centro",
                    "descripcion" to "Zona de prueba activa.",
                    "latitud" to -17.3867,
                    "longitud" to -66.1553,
                    "radio" to 500.0,   // 500m de radio — te cubre seguro
                    "nivel" to "alto",
                    "tipo" to "robo",
                    "activa" to true
                ),
                "zona_test_ubicacion_real" to mapOf(
                    "nombre" to "Zona Norte Cbba - Av. simon Lopez",
                    "descripcion" to "Zona de prueba activa cerca de tu ubicación.",
                    "latitud" to -17.3622,   // ← tu lat exacta
                    "longitud" to -66.1818,  // ← tu lon exacta
                    "radio" to 500.0,
                    "nivel" to "alto",
                    "tipo" to "robo",
                    "activa" to true
                )
            )

            db.child("zonas_peligrosas")
                .updateChildren(zonas)
                .await()

            println("✅ 10 zonas peligrosas insertadas correctamente")

        } catch (e: Exception) {
            println("❌ Error insertando zonas: ${e.message}")
        }
    }
}