package com.championstar.soccer.data.repository

import android.content.Context
import com.championstar.soccer.data.model.GameEvent
import com.championstar.soccer.data.model.Player
import com.championstar.soccer.data.model.Trigger // <-- PERBAIKAN 1: TAMBAHKAN IMPORT INI
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

object EventRepository {

    private var allEvents: List<GameEvent> = emptyList()

    fun init(context: Context) {
        if (allEvents.isNotEmpty()) return

        try {
            val jsonString = context.assets.open("events.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<GameEvent>>() {}.type
            allEvents = Gson().fromJson(jsonString, listType)
            println("EventRepository loaded ${allEvents.size} narrative events.")
        } catch (e: IOException) {
            e.printStackTrace()
            println("Error loading events.json: ${e.message}")
        }
    }

    fun getEventById(eventId: String): GameEvent? {
        return allEvents.find { it.eventId == eventId }
    }

    fun getEligibleEvents(player: Player): List<GameEvent> {
        return allEvents.filter { event ->
            event.triggers.all { trigger ->
                isTriggerMet(player, trigger)
            }
        }
    }

    private fun isTriggerMet(player: Player, trigger: Trigger): Boolean {
        // PERBAIKAN 2: Gunakan ?.let atau elvis operator (?:) untuk menangani nilai null dengan aman
        return when (trigger.type) {
            "careerState" -> player.careerState.name == trigger.value
            "minReputation" -> player.attributes.personal.reputation >= (trigger.min ?: 0)
            "maxReputation" -> player.attributes.personal.reputation <= (trigger.max ?: 100)
            "minCash" -> player.cash >= (trigger.min ?: 0.0).toDouble()
            "attributeCheck" -> {
                val attrValue = when(trigger.attribute) {
                    "POSITIONING" -> player.attributes.mental.positioning
                    "FINISHING" -> player.attributes.technical.finishing
                    // Tambahkan atribut lain di sini jika diperlukan
                    else -> 5
                }
                attrValue <= (trigger.max ?: 100) && attrValue >= (trigger.min ?: 0)
            }
            "timeOfYear" -> {
                // Logika untuk jendela transfer bisa ditambahkan di sini nanti
                true // Untuk sekarang, selalu lolos
            }
            else -> false
        }
    }
}