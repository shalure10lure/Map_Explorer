package com.ucb.mapexplorer.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.ucb.mapexplorer.core.data.db.AppDatabase
import com.ucb.mapexplorer.core.data.db.getDatabaseBuilder
import com.ucb.mapexplorer.core.utils.ConnectivityObserver
import com.ucb.mapexplorer.core.utils.IosConnectivityObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual val platformModule: Module = module {
    single<AppDatabase> {
        val path = documentDirectory() + "/map_explorer.db"
        Room.databaseBuilder<AppDatabase>(name = path)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    single { get<AppDatabase>().getAuthDao() }
    single { get<AppDatabase>().getTileDao() }
    single { get<AppDatabase>().getPlaceDao() }
    single { get<AppDatabase>().getLugarFavoritoDao() }
    single { get<AppDatabase>().getLugarGuardadoDao() }
    single { get<AppDatabase>().getZonaPeligrosaDao() }

    single { IosConnectivityObserver() } bind ConnectivityObserver::class
}

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val fileManager = NSFileManager.defaultManager
    val url = fileManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    return requireNotNull(url?.path)
}
