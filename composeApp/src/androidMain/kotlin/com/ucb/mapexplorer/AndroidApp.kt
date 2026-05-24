package com.ucb.mapexplorer

import android.app.Application
import android.preference.PreferenceManager
import com.ucb.mapexplorer.di.getModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import org.osmdroid.config.Configuration

class AndroidApp: Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Configuración global de OsmDroid
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        Configuration.getInstance().userAgentValue = packageName

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@AndroidApp)
            modules(getModules())
        }
    }
}
