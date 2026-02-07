package com.championstar.soccer.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.championstar.soccer.data.entities.StoryEventEntity

@Dao
interface StoryEventDao {
    @Query("SELECT * FROM story_event_table ORDER BY timestamp DESC")
    fun getAllEvents(): LiveData<List<StoryEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: StoryEventEntity)

    @Update
    suspend fun update(event: StoryEventEntity)
}