package com.championstar.soccer.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story_event_table")
data class StoryEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val choicesJson: String, // Stores list of Choice objects
    val isCompleted: Boolean = false,
    val outcomeJson: String? = null, // Stores result
    val timestamp: Long = System.currentTimeMillis()
)