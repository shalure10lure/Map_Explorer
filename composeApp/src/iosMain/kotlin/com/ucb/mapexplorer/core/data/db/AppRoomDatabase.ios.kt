package com.ucb.mapexplorer.core.data.db

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask


actual fun getDatabaseBuilder(ctx: Any?): RoomDatabase.Builder<AppDatabase> {

    val path = documentDirectory() + "/my_room.db"

    return Room.databaseBuilder(
        name = path
    )
}

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
fun documentDirectory(): String {
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