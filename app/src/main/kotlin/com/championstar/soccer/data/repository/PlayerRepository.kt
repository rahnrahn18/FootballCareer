package com.championstar.soccer.data.repository

import androidx.lifecycle.LiveData
import com.championstar.soccer.data.dao.PlayerDao
import com.championstar.soccer.data.entities.PlayerEntity

class PlayerRepository(private val playerDao: PlayerDao) {
    val player: LiveData<PlayerEntity?> = playerDao.getPlayer()

    suspend fun getPlayerSync(): PlayerEntity? = playerDao.getPlayerSync()

    suspend fun update(player: PlayerEntity) {
        playerDao.update(player)
    }

    suspend fun insert(player: PlayerEntity) {
        playerDao.insert(player)
    }
}