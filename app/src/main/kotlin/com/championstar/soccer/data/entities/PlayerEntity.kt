package com.championstar.soccer.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.championstar.soccer.data.converters.Converters

@Entity(tableName = "player_table")
@TypeConverters(Converters::class)
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val age: Int = 17,
    val position: String,
    val overallRating: Int = 50,
    val money: Long = 0L,
    val week: Int = 1,
    val year: Int = 2024,

    // JSON Strings for complex objects
    val appearanceJson: String, // Holds avatar config

    // Map of skills e.g., "Speed" -> 60, "Shooting" -> 55
    val skills: Map<String, Int> = emptyMap(),

    val teamName: String = "Free Agent",
    val morale: Int = 80,
    val energy: Int = 100
)