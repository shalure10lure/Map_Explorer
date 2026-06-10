package com.ucb.mapexplorer.worker

import android.content.Context
import com.ucb.mapexplorer.core.session.Session
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ucb.mapexplorer.map.domain.repository.MapRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class LogUploadWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters), KoinComponent {

    private val mapRepository: MapRepository by inject()

    override suspend fun doWork(): Result {
        val uid = Session.uid ?: return Result.failure()

        println("🚀 Iniciando sincronización de Tiles en segundo plano...")

        return try {
            // 1. Sincronizar historial (Firebase -> Room) si está vacío
            mapRepository.downloadHistoryIfEmpty(uid)

            // 2. Aquí podrías agregar una función en tu repo para
            println("✅ Sincronización de fondo completada")
            Result.success()
        } catch (e: Exception) {
            println("❌ Error en worker: ${e.message}")
            Result.retry()
        }
    }
}