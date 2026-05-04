package com.ucb.mapexplorer.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.ucb.mapexplorer.core.data.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<AppDatabase> {
        val context = androidContext()
        val dbFile = context.getDatabasePath("dollar_db.db")
        Room.databaseBuilder<AppDatabase>(
            context = context,
            name = dbFile.absolutePath
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    single { get<AppDatabase>().getDao() }
}