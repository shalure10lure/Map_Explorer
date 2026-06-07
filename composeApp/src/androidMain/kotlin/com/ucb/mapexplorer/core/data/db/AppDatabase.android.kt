package com.ucb.mapexplorer.core.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.execSQL

/** Migración 3→6: Actualiza la tabla de tiles */
val MIGRATION_3_6 = object : Migration(3, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        migrateCommon(database::execSQL)
    }

    override fun migrate(connection: SQLiteConnection) {
        migrateCommon(connection::execSQL)
    }

    private fun migrateCommon(execSQL: (String) -> Unit) {
        execSQL("""
            CREATE TABLE IF NOT EXISTS tiles_descubiertos_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                uid TEXT NOT NULL,
                tileX INTEGER NOT NULL,
                tileY INTEGER NOT NULL,
                descubiertoEn INTEGER NOT NULL,
                vecesVisitado INTEGER NOT NULL,
                ultimoIngreso INTEGER NOT NULL,
                sincronizado INTEGER NOT NULL DEFAULT 0
            )
        """)
        execSQL("""
            INSERT INTO tiles_descubiertos_new
            (id, uid, tileX, tileY, descubiertoEn, vecesVisitado, ultimoIngreso, sincronizado)
            SELECT id, uid, tileX, tileY, descubiertoEn, vecesVisitado, ultimoIngreso, sincronizado
            FROM tiles_descubiertos
        """)
        execSQL("DROP TABLE tiles_descubiertos")
        execSQL("ALTER TABLE tiles_descubiertos_new RENAME TO tiles_descubiertos")
        execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS index_tiles_uid_x_y
            ON tiles_descubiertos (uid, tileX, tileY)
        """)
    }
}

/** Migración 6→7: Crea las tablas de favoritos y guardados */
val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        migrateCommon(database::execSQL)
    }

    override fun migrate(connection: SQLiteConnection) {
        migrateCommon(connection::execSQL)
    }

    private fun migrateCommon(execSQL: (String) -> Unit) {
        // Tabla lugares_favoritos
        execSQL("""
            CREATE TABLE IF NOT EXISTS lugares_favoritos (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                uid TEXT NOT NULL,
                lugarId TEXT NOT NULL,
                nombre TEXT NOT NULL,
                categoria TEXT NOT NULL,
                latitud REAL NOT NULL,
                longitud REAL NOT NULL,
                iconoCategoria TEXT NOT NULL,
                agregadoEn INTEGER NOT NULL,
                sincronizado INTEGER NOT NULL DEFAULT 0
            )
        """)
        execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS index_favoritos_uid_lugarId
            ON lugares_favoritos (uid, lugarId)
        """)

        // Tabla lugares_guardados
        execSQL("""
            CREATE TABLE IF NOT EXISTS lugares_guardados (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                uid TEXT NOT NULL,
                lugarId TEXT NOT NULL,
                nombre TEXT NOT NULL,
                categoria TEXT NOT NULL,
                latitud REAL NOT NULL,
                longitud REAL NOT NULL,
                iconoCategoria TEXT NOT NULL,
                guardadoEn INTEGER NOT NULL,
                sincronizado INTEGER NOT NULL DEFAULT 0
            )
        """)
        execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS index_guardados_uid_lugarId
            ON lugares_guardados (uid, lugarId)
        """)
    }
}

actual fun getDatabaseBuilder(ctx: Any?): RoomDatabase.Builder<AppDatabase> {
    val appContext = (ctx as Context).applicationContext
    val dbFile = appContext.getDatabasePath("map_explorer.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
        .addMigrations(MIGRATION_3_6, MIGRATION_6_7)
        .fallbackToDestructiveMigration(true)
}
