package com.championstar.soccer.data.local

import android.content.Context
import com.championstar.soccer.domain.models.League
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.simulation.engine.TimeEngine
import com.championstar.soccer.simulation.engine.WorldGenerator
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

/**
 * GameStorage
 *
 * Handles the saving and loading of the entire game state (World + Player Career).
 * Uses JSON serialization for simplicity and flexibility.
 */
object GameStorage {

    private const val SAVE_FILE_NAME = "career_save_v1.json"
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    data class GameState(
        val player: Player,
        val leagues: List<League>,
        val currentDateString: String
    )

    /**
     * Checks if a save file exists.
     */
    fun hasSaveGame(context: Context): Boolean {
        val file = File(context.filesDir, SAVE_FILE_NAME)
        return file.exists()
    }

    /**
     * Saves the current game state.
     */
    fun saveGame(context: Context, player: Player, leagues: List<League>) {
        val state = GameState(
            player = player,
            leagues = leagues,
            currentDateString = TimeEngine.currentDate.toString()
        )
        val json = gson.toJson(state)
        context.openFileOutput(SAVE_FILE_NAME, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }

    /**
     * Loads the game state. Returns null if no save found or error.
     */
    fun loadGame(context: Context): GameState? {
        val file = File(context.filesDir, SAVE_FILE_NAME)
        if (!file.exists()) return null

        return try {
            val json = file.readText()
            val type = object : TypeToken<GameState>() {}.type
            gson.fromJson<GameState>(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Deletes the current save (e.g., Starting New Game).
     */
    fun deleteSave(context: Context) {
        val file = File(context.filesDir, SAVE_FILE_NAME)
        if (file.exists()) {
            file.delete()
        }
    }
}
