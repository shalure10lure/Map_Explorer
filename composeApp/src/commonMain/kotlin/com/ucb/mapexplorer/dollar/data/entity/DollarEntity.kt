package com.ucb.mapexplorer.dollar.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "dollars")
data class DollarEntity(
    @ColumnInfo(name = "dollar_official")
    var dollarOfficial: String? = null,


    @ColumnInfo(name = "dollar_parallel")
    var dollarParallel: String? = null,


    @ColumnInfo(name = "timestamp")
    var timestamp: Long = 0)
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}
