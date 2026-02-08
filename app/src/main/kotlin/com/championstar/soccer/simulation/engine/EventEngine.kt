package com.championstar.soccer.simulation.engine

import com.championstar.soccer.core.math.GameMath
import com.championstar.soccer.data.static.EventDatabase
import com.championstar.soccer.data.static.EventDatabase.EventCategory
import com.championstar.soccer.domain.models.Player
import kotlin.random.Random

data class RandomEvent(
    val title: String,
    val description: String,
    val choices: List<EventChoice>
)

data class EventChoice(
    val text: String,
    val consequence: (Player) -> String
)

object EventEngine {

    /**
     * Generates a weekly random event based on player status.
     * Tells the "Zero to Hero" story through dilemmas.
     * Modified to pull from the massive EventDatabase.
     */
    fun generateWeeklyEvent(player: Player): RandomEvent? {
        // 20% chance of event per week
        // if (!GameMath.chance(0.2)) return null
        // The user wants a "Daily Event" loop, so we should always return SOMETHING if asked.
        // However, `TimeEngine` calls this specifically when NO match is scheduled.
        // So let's make it more deterministic or context-aware.

        // Determine Context
        val possibleCategories = mutableListOf<EventCategory>()
        possibleCategories.add(EventCategory.FAMILY) // Always possible

        if (player.agent != null) {
            possibleCategories.add(EventCategory.AGENT)
        }

        // Randomly pick a category
        val category = possibleCategories.random()

        // Fetch event from DB
        val event = EventDatabase.getByCategory(category)

        // Fallback to legacy generation if null (shouldn't happen with our DB)
        return event ?: generateLegacyEvent(player)
    }

    /**
     * Specifically generates a MATCH event for Match Days.
     */
    fun generateMatchEvent(player: Player): RandomEvent? {
        return EventDatabase.getByCategory(EventCategory.MATCH)
    }

    private fun generateLegacyEvent(player: Player): RandomEvent? {
         // --- Low Reputation / Early Career Events ---
        if (player.reputation < 2.0) {
            return RandomEvent(
                "Extra Training Opportunity",
                "The coach is offering extra sessions after practice. It's grueling work.",
                listOf(
                    EventChoice("Attend (Cost: 10 Stamina)") { p ->
                        p.stamina = (p.stamina - 10.0).coerceAtLeast(0.0)
                        p.overallRating += 0.2
                        "Coach is impressed. Skill +0.2, Stamina -10."
                    },
                    EventChoice("Skip and Rest") { p ->
                        p.stamina = (p.stamina + 5.0).coerceAtMost(100.0)
                        "You feel rested. Stamina +5."
                    }
                )
            )
        }
        return null
    }
}
