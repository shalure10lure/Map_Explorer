package com.ucb.mapexplorer.core.data.db


import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Migración de versión 3 → 4:
// - Se elimina columna porcentajeExplorado (no soportado en SQLite sin recrear tabla)
// - Se recrea la tabla con el nuevo esquema
val MIGRATION_3_5 = object : Migration(3, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Crear tabla nueva con esquema correcto
        database.execSQL("""
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
        // Copiar datos existentes (se pierde porcentajeExplorado, sin impacto)
        database.execSQL("""
            INSERT INTO tiles_descubiertos_new
            (id, uid, tileX, tileY, descubiertoEn, vecesVisitado, ultimoIngreso, sincronizado)
            SELECT id, uid, tileX, tileY, descubiertoEn, vecesVisitado, ultimoIngreso, sincronizado
            FROM tiles_descubiertos
        """)
        // Eliminar tabla vieja y renombrar nueva
        database.execSQL("DROP TABLE tiles_descubiertos")
        database.execSQL("ALTER TABLE tiles_descubiertos_new RENAME TO tiles_descubiertos")
        // Recrear el índice único
        database.execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS index_tiles_uid_x_y
            ON tiles_descubiertos (uid, tileX, tileY)
        """)
    }
}

actual fun getDatabaseBuilder(ctx: Any?): RoomDatabase.Builder<AppDatabase> {
    val appContext = (ctx as Context).applicationContext
    val dbFile = appContext.getDatabasePath("map_explorer.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    ).addMigrations(MIGRATION_3_5)  // ← agregar esto
}