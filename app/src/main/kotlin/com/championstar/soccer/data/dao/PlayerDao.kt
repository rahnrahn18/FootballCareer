package com.championstar.soccer.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.championstar.soccer.data.entities.PlayerEntity

@Dao
interface PlayerDao {
    @Query("SELECT * FROM player_table ORDER BY id DESC LIMIT 1")
    fun getPlayer(): LiveData<PlayerEntity?>

    @Query("SELECT * FROM player_table ORDER BY id DESC LIMIT 1")
    suspend fun getPlayerSync(): PlayerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(player: PlayerEntity)

    @Update
    suspend fun update(player: PlayerEntity)

    @Query("DELETE FROM player_table")
    suspend fun deleteAll()
}