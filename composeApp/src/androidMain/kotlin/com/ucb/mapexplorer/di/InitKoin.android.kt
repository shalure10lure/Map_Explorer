package com.ucb.mapexplorer.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.ucb.mapexplorer.core.data.db.AppDatabase
import com.ucb.mapexplorer.core.data.db.MIGRATION_3_6
import com.ucb.mapexplorer.core.data.db.MIGRATION_6_7
import com.ucb.mapexplorer.core.data.db.MIGRATION_7_8
import com.ucb.mapexplorer.core.utils.ConnectivityObserver
import com.ucb.mapexplorer.core.utils.NetworkConnectivityObserver
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
    single<AppDatabase> {
        val context = androidContext()
        val dbFile = context.getDatabasePath("map_explorer.db")
        Room.databaseBuilder<AppDatabase>(
            context = context,
            name = dbFile.absolutePath
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .addMigrations(MIGRATION_3_6, MIGRATION_6_7, MIGRATION_7_8)
            .build()
    }

    single { get<AppDatabase>().getAuthDao() }
    single { get<AppDatabase>().getTileDao() }
    single { get<AppDatabase>().getPlaceDao() }
    single { get<AppDatabase>().getLugarFavoritoDao() }
    single { get<AppDatabase>().getLugarGuardadoDao() }
    single { get<AppDatabase>().getZonaPeligrosaDao() }

    single { NetworkConnectivityObserver(androidContext()) } bind ConnectivityObserver::class
}