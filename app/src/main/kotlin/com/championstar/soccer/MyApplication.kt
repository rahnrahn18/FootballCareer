package com.championstar.soccer

import android.app.Application
import com.championstar.soccer.data.AppDatabase
import com.championstar.soccer.data.repository.BusinessRepository
import com.championstar.soccer.data.repository.PlayerRepository
import com.championstar.soccer.data.repository.StoryRepository
import com.championstar.soccer.game.engine.GameEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MyApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val playerRepository by lazy { PlayerRepository(database.playerDao()) }
    val businessRepository by lazy { BusinessRepository(database.businessDao()) }
    val storyRepository by lazy { StoryRepository(database.storyEventDao()) }

    val gameEngine by lazy { GameEngine(playerRepository, businessRepository) }
}