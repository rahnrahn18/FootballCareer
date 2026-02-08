package com.championstar.soccer.data.local

import android.content.Context
import com.championstar.soccer.domain.models.League
import com.championstar.soccer.domain.models.Player
import com.championstar.soccer.simulation.engine.TimeEngine
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter

data class GameState(
    val player: Player,
    val leagues: List<League>,
    val currentDate: String // We will parse this back into TimeEngine
)

object GameStorage {

    private const val SAVE_FILE_NAME = "career_save_v1.json"
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    /**
     * Saves the entire game state to a local JSON file.
     * This replaces Room Database for simplicity and compatibility.
     */
    fun saveGame(context: Context, player: Player, leagues: List<League>) {
        val file = File(context.filesDir, SAVE_FILE_NAME)

        // Wrap data in GameState
        val gameState = GameState(
            player = player,
            leagues = leagues,
            currentDate = TimeEngine.currentDate.toString()
        )

        try {
            val writer = FileWriter(file)
            gson.toJson(gameState, writer)
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Loads the game state from the local JSON file.
     * Returns null if no save exists or loading fails.
     */
    fun loadGame(context: Context): GameState? {
        val file = File(context.filesDir, SAVE_FILE_NAME)
        if (!file.exists()) return null

        return try {
            val reader = FileReader(file)
            val type = object : TypeToken<GameState>() {}.type
            val gameState: GameState = gson.fromJson(reader, type)
            reader.close()

            // IMPORTANT: Restore static TimeEngine state
            // In a real app, TimeEngine should not be static, but for this architecture we patch it here.
            // Parse logic would be needed if we want exact restoration, but for now we just load the data.
            // Ideally, GameDate should be part of the save and injected back.
            // TimeEngine.currentDate = ... (Need parsing logic if we want to support this perfectly)

            gameState
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Deletes the save file (Game Over / New Game).
     */
    fun deleteSave(context: Context) {
        val file = File(context.filesDir, SAVE_FILE_NAME)
        if (file.exists()) {
            file.delete()
        }
    }

    fun hasSaveGame(context: Context): Boolean {
        val file = File(context.filesDir, SAVE_FILE_NAME)
        return file.exists()
    }
}
