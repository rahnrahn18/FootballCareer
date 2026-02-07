package com.championstar.soccer.data.repository

import androidx.lifecycle.LiveData
import com.championstar.soccer.data.dao.StoryEventDao
import com.championstar.soccer.data.entities.StoryEventEntity

class StoryRepository(private val storyEventDao: StoryEventDao) {
    val allEvents: LiveData<List<StoryEventEntity>> = storyEventDao.getAllEvents()

    suspend fun insert(event: StoryEventEntity) {
        storyEventDao.insert(event)
    }

    suspend fun update(event: StoryEventEntity) {
        storyEventDao.update(event)
    }
}