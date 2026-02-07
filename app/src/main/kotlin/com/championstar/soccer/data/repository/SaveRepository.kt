package com.championstar.soccer.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.championstar.soccer.data.model.Player
import com.google.gson.Gson

object SaveRepository {

    private const val PREFS_NAME = "championstar_save"
    private const val KEY_SLOT1 = "slot1_json"

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /* -------------- SLOT 1 -------------- */
    fun saveSlot1(player: Player) {
        // Implementasi save yang disederhanakan dan berfungsi
        val playerJson = gson.toJson(player)
        prefs.edit().putString(KEY_SLOT1, playerJson).apply()
    }

    fun loadSlot1(): Player? {
        val json = prefs.getString(KEY_SLOT1, null) ?: return null
        return gson.fromJson(json, Player::class.java)
    }

    fun hasSlot1(): Boolean = prefs.contains(KEY_SLOT1)

    // Catatan: Save/Load untuk LeagueManager dan GameCalendar akan kita implementasikan
    // dalam satu langkah terpisah setelah sistem musim stabil.
}