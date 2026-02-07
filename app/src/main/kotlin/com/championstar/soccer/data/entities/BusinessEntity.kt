package com.championstar.soccer.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business_table")
data class BusinessEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: String, // e.g., "Real Estate", "Stock", "Media"
    val baseCost: Long,
    val baseIncome: Long,
    val level: Int = 0, // 0 = not owned, 1+ = owned
    val description: String = ""
)