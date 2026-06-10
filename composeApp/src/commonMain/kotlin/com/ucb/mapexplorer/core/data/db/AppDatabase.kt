package com.ucb.mapexplorer.core.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.ucb.mapexplorer.auth.data.dao.AuthDao
import com.ucb.mapexplorer.auth.data.entity.UserEntity
import com.ucb.mapexplorer.dangerzone.data.dao.ZonaPeligrosaDao
import com.ucb.mapexplorer.dangerzone.data.entity.ZonaPeligrosaEntity
import com.ucb.mapexplorer.map.data.dao.TileDao
import com.ucb.mapexplorer.map.data.entity.TileEntity
import com.ucb.mapexplorer.nearbyplaces.data.dao.LugarFavoritoDao
import com.ucb.mapexplorer.nearbyplaces.data.dao.LugarGuardadoDao
import com.ucb.mapexplorer.nearbyplaces.data.dao.PlaceDao
import com.ucb.mapexplorer.nearbyplaces.data.entity.LugarCacheEntity
import com.ucb.mapexplorer.nearbyplaces.data.entity.LugarFavoritoEntity
import com.ucb.mapexplorer.nearbyplaces.data.entity.LugarGuardadoEntity

@Database(
    entities = [
        UserEntity::class,
        TileEntity::class,
        LugarCacheEntity::class,
        LugarFavoritoEntity::class,
        LugarGuardadoEntity::class,
        ZonaPeligrosaEntity::class
    ],
    version = 8,exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getAuthDao(): AuthDao

    abstract fun getTileDao(): TileDao

    abstract fun getPlaceDao(): PlaceDao
    abstract fun getLugarFavoritoDao(): LugarFavoritoDao
    abstract fun getLugarGuardadoDao(): LugarGuardadoDao
    abstract fun getZonaPeligrosaDao(): ZonaPeligrosaDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

expect fun getDatabaseBuilder(ctx: Any? = null): RoomDatabase.Builder<AppDatabase>